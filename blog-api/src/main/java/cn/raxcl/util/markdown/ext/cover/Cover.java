package cn.raxcl.util.markdown.ext.cover;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;

/**
 * A cover node containing text and other inline nodes nodes as children.
 * @author Raxcl
 * @date 2022-03-15 12:06:38
 */
public class Cover extends CustomNode implements Delimited {
	private static final String DELIMITER = "%%";

	@Override
	public String getOpeningDelimiter() {
		return DELIMITER;
	}

	@Override
	public String getClosingDelimiter() {
		return DELIMITER;
	}
}
