package cn.raxcl.util.markdown.ext.curtain.internal;

import cn.raxcl.constant.CommonConstant;
import cn.raxcl.util.markdown.ext.cover.internal.CoverDelimiterProcessor;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;
import cn.raxcl.util.markdown.ext.curtain.Curtain;

/**
 * 定界
 * @author Raxcl
 * @date 2022-03-15 11:34:07
 */
public class CurtainDelimiterProcessor implements DelimiterProcessor {
    @Override
    public char getOpeningCharacter() {
        return '@';
    }

    @Override
    public char getClosingCharacter() {
        return '@';
    }

    @Override
    public int getMinLength() {
        return 2;
    }

	@Override
	public int process(DelimiterRun openingRun, DelimiterRun closingRun) {
		if (openingRun.length() >= CommonConstant.TWO && closingRun.length() >= CommonConstant.TWO) {
			// Use exactly two delimiters even if we have more, and don't care about internal openers/closers.
			Text opener = openingRun.getOpener();
			// Wrap nodes between delimiters in hei_mu.
			Node curtain = new Curtain();
			return CoverDelimiterProcessor.sourceSpansMethod(openingRun, closingRun, opener, curtain);
		}
		return 0;
	}
}
