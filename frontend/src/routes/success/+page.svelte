<script lang="ts">
  import { goto } from "$app/navigation";
  import Skeleton from "$lib/components/ui/skeleton/skeleton.svelte";

  let spotifyCode = $state<null | string>(null);

  const getCodeFromUrl = () => {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("code");
  };

  $effect(() => {
    const code = getCodeFromUrl();
    if (code) {
      spotifyCode = code;
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
      const data = await response.json();
      goto("/");
    } else {
      console.error("Failed to send code");
    }
  }
</script>

<main>
  <h1>Redirecting</h1>
  <Skeleton class="w-full max-w-2xl mx-auto h-36" />
</main>
