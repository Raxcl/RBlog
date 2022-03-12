<template>
	<div id="app">
		<router-view />
		<Live2d/>
	</div>
</template>

<script>
	import Live2d from '@/components/live2d/live2d'


	export default {
		name: 'App',
		components: {
			Live2d
		}
	}
</script>

<style>
	@import "./common/font/font.css";
	#app{
		font-family: "Delius Swash Caps", cursive;
	}
</style>
