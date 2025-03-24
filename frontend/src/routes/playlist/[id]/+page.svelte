<script lang="ts">
  import { page } from "$app/state";
  import { apiClient } from "$lib/clients/api-client";
  import PlaylistView from "$lib/components/spotify/playlist-view.svelte";
  import { toast } from "svelte-sonner";
  import type { PageProps } from "../$types";
  import type { Playlist } from "$lib/types/spotify/playlist";
  import { Button } from "$lib/components/ui/button";

  let { data }: PageProps = $props();
  let playlist = $state<Playlist | null>(null);

  $effect(() => {
    const fetchPlaylist = async () => {
      if ("id" in data) {
        const response = await apiClient.get<Playlist>(
          `/playlist/${data.id}`,
          page.data.user.jwt_token
        );

        if (response.error != null) {
          toast.error(response.error.message);
        } else if (response.data != null) {
          toast.success("Playlist fetched successfully!");
          playlist = response.data;
        }
      } else {
        console.error("No id found in data");
      }
    };

    fetchPlaylist()
      .then(() => {})
      .catch((error) => {
        toast.error("Error fetching playlist: " + error.message);
      });
  });
</script>

<div class="flex flex-col gap-4 justify-center items-center">
  <PlaylistView {playlist} />
  <Button variant="outline" class="w-fit">
    <a href="/" class="text-white">
      {playlist === null ? "Create Playlist" : "Generate another playlist"}
    </a>
  </Button>
</div>
