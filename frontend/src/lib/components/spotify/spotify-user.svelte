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
    <div class="flex font-bold text-2xl">
      <h2>
        Hello
        <a
          href={spotifyUser.external_urls.spotify}
          target="_blank"
          class="hover:underline"
        >
          {spotifyUser.display_name}
        </a>
        !
      </h2>
      <br />
    </div>
  {:else}
    <p>Loading...</p>
  {/if}
</main>
