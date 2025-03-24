<script lang="ts">
  import { goto } from "$app/navigation";
  import LoadingSpinner from "$lib/components/loading-spinner.svelte";
  import { toast } from "svelte-sonner";

  const getCodeFromUrl = () => {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("code");
  };

  $effect(() => {
    const code = getCodeFromUrl();
    if (code) {
      sendCode(code);
    }
  });

  async function sendCode(code: string) {
    const response = await fetch("http://localhost:8080/token", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ code }),
      credentials: "include",
    });

    if (response.ok) {
      goto("/");
    } else {
      toast.error("Failed to send code");
    }
  }
</script>

<main class="flex gap-4">
  <LoadingSpinner />
  <p>Redirecting</p>
</main>
