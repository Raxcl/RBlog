module.exports = {
	// publicPath:'./', //从当前目录下获取资源（由于静态资源不太一起，加了刷新页面会白屏）
	outputDir: process.env.outputDir,    // 打包生成目录
	configureWebpack: {
		resolve: {
			alias: {
				'assets': '@/assets',
				'common': '@/common',
				'components': '@/components',
				'api': '@/api',
				'views': '@/views',
				'plugins': '@/plugins'
			}
		}
	}
}