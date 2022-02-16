package cn.raxcl.util.markdown;

import cn.raxcl.util.markdown.ext.cover.CoverExtension;
import cn.raxcl.util.markdown.ext.curtain.CurtainExtension;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description: Markdown转换
 * @author Raxcl
 * @date 2022-01-07 19:32:33
 */
public class MarkdownUtils {
	private MarkdownUtils(){}
	/**
	 * 为h标签生成id 供tocbot目录生成
	 */
	private static final Set<Extension> HEADING_ANCHOR_EXTENSIONS = Collections.singleton(HeadingAnchorExtension.create());
	/**
	 * 转换table的HTML
	 */
	private static final List<Extension> TABLE_EXTENSION = Collections.singletonList(TablesExtension.create());
	private static final String JI = "#";
	/**
	 * 任务列表
	 */
	private static final Set<Extension> TASK_LIST_EXTENSION = Collections.singleton(TaskListItemsExtension.create());
	/**
	 * 删除线
	 */
	private static final Set<Extension> DEL_EXTENSION = Collections.singleton(StrikethroughExtension.create());
	/**
	 * 黑幕
	 */
	private static final Set<Extension> HEI_MU_EXTENSION = Collections.singleton(CurtainExtension.create());
	/**
	 * 遮盖层
	 */
	private static final Set<Extension> COVER_EXTENSION = Collections.singleton(CoverExtension.create());

	private static final Parser PARSER = Parser.builder()
			.extensions(TABLE_EXTENSION)
			.extensions(TASK_LIST_EXTENSION)
			.extensions(DEL_EXTENSION)
			.extensions(HEI_MU_EXTENSION)
			.extensions(COVER_EXTENSION)
			.build();

	private static final HtmlRenderer RENDERER = HtmlRenderer.builder()
			.extensions(HEADING_ANCHOR_EXTENSIONS)
			.extensions(TABLE_EXTENSION)
			.extensions(TASK_LIST_EXTENSION)
			.extensions(DEL_EXTENSION)
			.extensions(HEI_MU_EXTENSION)
			.extensions(COVER_EXTENSION)
			.attributeProviderFactory(context -> new CustomAttributeProvider())
			.build();

	/**
	 * 自定义标签的属性
	 */
	private static class CustomAttributeProvider implements AttributeProvider {
		@Override
		public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
			//改变a标签的target属性为_blank
			if (node instanceof Link) {
				Link n = (Link) node;
				String destination = n.getDestination();
				//判断是否页内锚点跳转
				if (destination.startsWith(JI)) {
					attributes.put("class", "toc-link");//针对tocbot锚点跳转的class属性
				} else {
					//外部链接
					attributes.put("target", "_blank");
					attributes.put("rel", "external nofollow noopener");
				}
			}
			if (node instanceof TableBlock) {
				attributes.put("class", "ui celled table");//针对 semantic-ui 的class属性
			}
		}
	}

	/**
	 * markdown格式转换成HTML格式
	 */
	public static String markdownToHtml(String markdown) {
		Parser parser = Parser.builder().build();
		Node document = parser.parse(markdown);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		return renderer.render(document);
	}

	/**
	 * 增加扩展
	 */
	public static String markdownToHtmlExtensions(String markdown) {
		Node document = PARSER.parse(markdown);
		return RENDERER.render(document);
	}
}
