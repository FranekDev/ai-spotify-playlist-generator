<script lang="ts">
  import { Toaster } from "$lib/components/ui/sonner";
  import ThemeSwitch from "$lib/components/ui/theme-switch/theme-switch.svelte";
  import "../app.css";

  import { ModeWatcher } from "mode-watcher";
  let { children } = $props();
</script>

<ModeWatcher />
<Toaster />
<main class="container flex min-h-screen min-w-screen justify-center items-center">
	<div class="absolute top-4 right-4">
		
	<ThemeSwitch />
	</div>
  {@render children()}
</main>
