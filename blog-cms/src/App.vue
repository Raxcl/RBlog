<template>
	<div id="app">
		<router-view />
		<Live2d style="z-index:1703"/>
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
