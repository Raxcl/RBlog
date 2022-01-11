package cn.raxcl.util.markdown.ext.curtain;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;

/**
 * @Description: A heimu node containing text and other inline nodes nodes as children.
 * @author Raxcl
 * @date 2022-01-07 19:31:28
 */
public class Curtain extends CustomNode implements Delimited {
	private static final String DELIMITER = "@@";

	@Override
	public String getOpeningDelimiter() {
		return DELIMITER;
	}

	//TODO markdown 方法内部实现相同（怀疑是用的切面）， 后续有能力了再解决
	@Override
	public String getClosingDelimiter() {
		return DELIMITER;
	}
}
