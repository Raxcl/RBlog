module.exports = {
	publicPath:'./', //从当前目录下获取资源
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