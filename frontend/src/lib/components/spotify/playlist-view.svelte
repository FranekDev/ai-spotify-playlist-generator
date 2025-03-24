<script lang="ts">
  import type { Playlist } from "$lib/types/spotify/playlist";
  import { Link } from "@lucide/svelte";
  import ScrollArea from "../ui/scroll-area/scroll-area.svelte";
  import { Separator } from "../ui/separator";

  let { playlist }: { playlist: Playlist | null } = $props();

  const convertSecondsToTime = (duration: number) => {
    duration = Math.floor(duration / 1000);
    const minutes = Math.floor(duration / 60);
    const seconds = duration % 60;
    return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
  };
</script>

{#if playlist === null}
  <div class="flex flex-col gap-4 justify-center items-center">
    <p class="text-2xl font-bold">No playlist found</p>
    <p class="text-zinc-500">Please create a playlist first</p>
  </div>
{:else}
  <div class="flex flex-col gap-4">
    <div class="flex items-end gap-4">
      {#if playlist?.images}
        <img
          src={playlist?.images[0].url}
          alt={playlist?.name}
          class="w-32 h-32 rounded-md"
        />
      {/if}
      <div class="flex flex-col">
        <div class="flex items-center gap-2">
          <p class="text-2xl font-bold flex items-center p-0 h-fit">
            {playlist?.name}
          </p>
          <a
            class="text-zinc-500 hover:text-zinc-300 transition-all"
            href={playlist?.external_urls.spotify}
            target="_blank"
            rel="noopener noreferrer"
          >
            <Link class="w-4 h-4" />
          </a>
        </div>
        <div class="flex items-center gap-2 text-zinc-500 text-xs">
          <p>{playlist?.owner.display_name}</p>
          •
          <p>
            {playlist?.tracks.total}
            {(playlist?.tracks.total ?? 0 > 1) ? "songs" : "song"}
          </p>
        </div>
      </div>
    </div>

    <p>{playlist?.description}</p>
    <div class="flex flex-col gap-2">
      {#if playlist?.tracks.items}
        <ScrollArea class="h-[40rem] min-h-fit rounded-md border">
          {#each playlist.tracks.items as track, index}
            <div class="flex items justify-between p-4">
              <div class="flex items-center gap-4">
                <p class="text-zinc-500 text-xs">#{index + 1}</p>
                <img
                  src={track.track.album.images[0].url}
                  alt={track.track.name}
                  class="w-16 h-16 rounded-md"
                />
                <div class="flex flex-col">
                  <h2 class="text-lg font-semibold">
                    <a
                      href={track.track.external_urls.spotify}
                      class="hover:underline"
                      target="_blank"
                    >
                      {track.track.name}
                    </a>
                  </h2>
                  <p class="text-sm text-zinc-500">
                    {track.track.artists[0].name}
                  </p>
                </div>
              </div>
              <div class="flex items-center gap-4">
                <p class="text-sm text-zinc-500">{track.track.album.name}</p>
                <p class="text-sm text-zinc-500">
                  {convertSecondsToTime(track.track.duration_ms)}
                </p>
              </div>
            </div>
            {#if index < playlist.tracks.items.length - 1}
              <Separator />
            {/if}
          {/each}
        </ScrollArea>
      {/if}
    </div>
  </div>
{/if}
