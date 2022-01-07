package cn.raxcl.util.markdown.ext.heimu;

import cn.raxcl.util.markdown.ext.heimu.internal.HeimuDelimiterProcessor;
import cn.raxcl.util.markdown.ext.heimu.internal.HeimuHtmlNodeRenderer;
import cn.raxcl.util.markdown.ext.heimu.internal.HeimuTextContentNodeRenderer;
import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;

/**
 * @Description: 自定义黑幕拓展
 * @author Raxcl
 * @date 2022-01-07 19:31:47
 */
public class HeimuExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension, TextContentRenderer.TextContentRendererExtension {
	private HeimuExtension() {
	}


	public static Extension create() {
		return new HeimuExtension();
	}

	@Override
	public void extend(Parser.Builder parserBuilder) {
		parserBuilder.customDelimiterProcessor(new HeimuDelimiterProcessor());
	}

	@Override
	public void extend(HtmlRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(HeimuHtmlNodeRenderer::new);
	}

	@Override
	public void extend(TextContentRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(HeimuTextContentNodeRenderer::new);
	}
}
