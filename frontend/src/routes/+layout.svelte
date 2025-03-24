<script lang="ts">
  import { page } from "$app/state";
  import LogOut from "$lib/components/log-out.svelte";
  import { Toaster } from "$lib/components/ui/sonner";
  import ThemeSwitch from "$lib/components/ui/theme-switch/theme-switch.svelte";
  import { cn } from "$lib/utils";
  import "../app.css";
  import { ModeWatcher } from "mode-watcher";

  let { children } = $props();
</script>

<ModeWatcher />
<Toaster />
<main
  class="container flex min-h-screen min-w-screen justify-center items-center selection:bg-spotify"
>
  <div
    class={cn(
      "absolute flex top-4 px-4 w-full",
      page.data.user ? "justify-between" : "justify-end"
    )}
  >
    {#if page.data.user}
      <LogOut />
    {/if}
    <ThemeSwitch />
  </div>
  {@render children()}
</main>
