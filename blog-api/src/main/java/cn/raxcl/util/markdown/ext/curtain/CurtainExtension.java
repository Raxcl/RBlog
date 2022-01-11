package cn.raxcl.util.markdown.ext.curtain;

import cn.raxcl.util.markdown.ext.curtain.internal.CurtainDelimiterProcessor;
import cn.raxcl.util.markdown.ext.curtain.internal.CurtainHtmlNodeRenderer;
import cn.raxcl.util.markdown.ext.curtain.internal.CurtainTextContentNodeRenderer;
import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;

/**
 * @Description: 自定义黑幕拓展
 * @author Raxcl
 * @date 2022-01-07 19:31:47
 */
public class CurtainExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension, TextContentRenderer.TextContentRendererExtension {
	private CurtainExtension() {
	}


	public static Extension create() {
		return new CurtainExtension();
	}

	@Override
	public void extend(Parser.Builder parserBuilder) {
		parserBuilder.customDelimiterProcessor(new CurtainDelimiterProcessor());
	}

	@Override
	public void extend(HtmlRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(CurtainHtmlNodeRenderer::new);
	}

	@Override
	public void extend(TextContentRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(CurtainTextContentNodeRenderer::new);
	}
}
