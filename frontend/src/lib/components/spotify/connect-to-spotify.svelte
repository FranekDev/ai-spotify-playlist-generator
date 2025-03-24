<script lang="ts">
  import { Button } from "$lib/components/ui/button";
  import { PUBLIC_SPOTIFY_CLIENT_ID } from "$env/static/public";
  import LoadingSpinner from "../loading-spinner.svelte";

  let connecting = $state(false);

  function connectToSpotify() {
    connecting = true;
    const generateRandomString = (length: number) => {
      const possible =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
      const values = crypto.getRandomValues(new Uint8Array(length));
      return values.reduce((acc, x) => acc + possible[x % possible.length], "");
    };

    const state = generateRandomString(16);

    const clientId = PUBLIC_SPOTIFY_CLIENT_ID;
    const redirectUri = "http://localhost:5173/success";
    const scope =
      "user-read-private user-read-email playlist-modify-public playlist-modify-private playlist-read-private";
    const authUrl = new URL("https://accounts.spotify.com/authorize");

    const params = {
      response_type: "code",
      client_id: clientId,
      scope,
      redirect_uri: redirectUri,
      state,
    };

    authUrl.search = new URLSearchParams(params).toString();
    window.location.href = authUrl.toString();
  }
</script>

<Button
  class="bg-spotify hover:bg-spotify-dark"
  disabled={connecting}
  onclick={() => connectToSpotify()}
>
  {#if connecting}
    <LoadingSpinner /> Connecting...
  {:else}
    Connect to Spotify
  {/if}
</Button>
