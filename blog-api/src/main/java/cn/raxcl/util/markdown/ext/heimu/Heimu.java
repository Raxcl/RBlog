package cn.raxcl.util.markdown.ext.heimu;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;

/**
 * @Description: A heimu node containing text and other inline nodes nodes as children.
 * @author Raxcl
 * @date 2022-01-07 19:31:28
 */
public class Heimu extends CustomNode implements Delimited {
	private static final String DELIMITER = "@@";

	@Override
	public String getOpeningDelimiter() {
		return DELIMITER;
	}

	@Override
	//TODO
	public String getClosingDelimiter() {
		return DELIMITER;
	}
}
