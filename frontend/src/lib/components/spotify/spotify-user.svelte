<script lang="ts">
  import { apiClient } from "$lib/clients/api-client";
  import type { SpotifyUserDetail } from "$lib/types/spotify/spotify-user-detail";
  import type { User } from "$lib/types/user";

  let { user }: { user: User } = $props();

  let spotifyUser = $state<SpotifyUserDetail | null>(null);

  $effect(() => {
    const fetchData = async () => {
      const response = await apiClient.get<SpotifyUserDetail>(
        "/user",
        user.jwt_token!
      );

      if (response.data !== null) {
        spotifyUser = response.data;
      } else {
        console.error("Failed to fetch user data");
        console.error(response.error);
      }
    };
    fetchData();
  });
</script>

<main>
  {#if spotifyUser}
    <div>
      <h2>Hello {spotifyUser.display_name}!</h2>
      <p>
        Spotify URL: <a href={spotifyUser.external_urls.spotify} class="hover:underline"
          >Your profile link</a
        >
      </p>

      <br />
    </div>
  {:else}
    <p>Loading...</p>
  {/if}
</main>
