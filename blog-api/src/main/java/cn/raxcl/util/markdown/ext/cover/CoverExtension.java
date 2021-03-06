package cn.raxcl.util.markdown.ext.cover;

import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;
import cn.raxcl.util.markdown.ext.cover.internal.CoverDelimiterProcessor;
import cn.raxcl.util.markdown.ext.cover.internal.CoverHtmlNodeRenderer;
import cn.raxcl.util.markdown.ext.cover.internal.CoverTextContentNodeRenderer;

/**
 * 自定义遮盖层拓展
 * @author Raxcl
 * @date 2022-03-15 11:41:58
 */
public class CoverExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension, TextContentRenderer.TextContentRendererExtension {
	private CoverExtension() {
	}

	public static Extension create() {
		return new CoverExtension();
	}

	@Override
	public void extend(Parser.Builder parserBuilder) {
		parserBuilder.customDelimiterProcessor(new CoverDelimiterProcessor());
	}

	@Override
	public void extend(HtmlRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(CoverHtmlNodeRenderer::new);
	}

	@Override
	public void extend(TextContentRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(CoverTextContentNodeRenderer::new);
	}
}
