<script lang="ts">
  import { page } from "$app/state";
  import ConnectToSpotify from "$lib/components/spotify/connect-to-spotify.svelte";
  import CreatePlaylist from "$lib/components/spotify/create-playlist.svelte";
  import SpotifyUser from "$lib/components/spotify/spotify-user.svelte";
</script>

{#if page.data.user}
  <div class="w-full max-w-2xl mx-auto">
    <SpotifyUser user={page.data.user} />
    <CreatePlaylist data={page.data.form} />
  </div>
{:else}
  <ConnectToSpotify />
{/if}
