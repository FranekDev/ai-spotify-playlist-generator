package org.omsk.aispotifyplaylistgenerator.ai.clients

import io.netty.handler.timeout.ReadTimeoutException
import org.omsk.aispotifyplaylistgenerator.models.api.ApiError
import org.omsk.aispotifyplaylistgenerator.models.api.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.retry.Retry
import java.io.IOException
import java.time.Duration

@Component
class AnthropicClient(private val chatClient: ChatClient) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val MAX_RETRY_ATTEMPTS: Long = 3
    private val INITIAL_BACKOFF = Duration.ofSeconds(2)
    private val MAX_BACKOFF = Duration.ofSeconds(10)
    private val JITTER = 0.5
    private val PROMPT_TIMEOUT = Duration.ofSeconds(600)

    private val retrySpec = Retry.backoff(MAX_RETRY_ATTEMPTS, INITIAL_BACKOFF)
        .maxBackoff(MAX_BACKOFF)
        .jitter(JITTER)
        .filter { throwable ->
            throwable is ReadTimeoutException ||
                    throwable.cause is ReadTimeoutException ||
                    (throwable is ResourceAccessException && throwable.cause is IOException)
        }
        .onRetryExhaustedThrow { _, retrySignal ->
            retrySignal.failure()
        }
        .doBeforeRetry { rs ->
            logger.warn("Retrying AI request (attempt: ${rs.totalRetries() + 1}) after error: ${rs.failure().message}")
        }

    fun prompt(message: String): Mono<ApiResponse<String>> {
        logger.info("Sending prompt to Anthropic API")

        return Mono.fromCallable {
            chatClient.prompt()
                .user(message)
                .call()
                .content()
        }
            .subscribeOn(Schedulers.boundedElastic())
            .timeout(PROMPT_TIMEOUT)
            .map { ApiResponse(it, null) }
            .retryWhen(retrySpec)
            .doOnError { error ->
                logger.error("Error in Anthropic API call", error)
            }
            .onErrorResume { error ->
                val errorMessage = when (error) {
                    is ReadTimeoutException -> "AI processing timed out. Your request might be too complex."
                    is ResourceAccessException -> "Connection to AI service failed: ${error.message}"
                    else -> "AI processing error: ${error.message}"
                }
                logger.error(errorMessage, error)
                Mono.just(ApiResponse(null, ApiError(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.value())))
            }
    }
}