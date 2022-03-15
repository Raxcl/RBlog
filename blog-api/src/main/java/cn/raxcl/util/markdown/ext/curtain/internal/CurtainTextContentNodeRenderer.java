package cn.raxcl.util.markdown.ext.curtain.internal;

import org.commonmark.node.Node;
import org.commonmark.renderer.text.TextContentNodeRendererContext;
import org.commonmark.renderer.text.TextContentWriter;

/**
 * 文本节点渲染
 * @author Raxcl
 * @date 2022-03-15 11:41:07
 */
public class CurtainTextContentNodeRenderer extends AbstractCurtainNodeRenderer {
    private final TextContentNodeRendererContext context;
    private final TextContentWriter textContent;

    public CurtainTextContentNodeRenderer(TextContentNodeRendererContext context) {
        this.context = context;
        this.textContent = context.getWriter();
    }

    @Override
    public void render(Node node) {
        textContent.write('/');
        renderChildren(node);
        textContent.write('/');
    }

    private void renderChildren(Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            Node next = node.getNext();
            context.render(node);
            node = next;
        }
    }
}
