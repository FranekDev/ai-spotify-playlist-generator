<script lang="ts">
  import type { PageProps } from "../$types";

  let { data }: PageProps = $props();

  $effect(() => {
    console.log("data", data);
  });
</script>

<h1>playlist</h1>