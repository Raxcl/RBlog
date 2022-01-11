package cn.raxcl.util.markdown.ext.curtain.internal;

import cn.raxcl.constant.CommonConstant;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;
import cn.raxcl.util.markdown.ext.curtain.Curtain;

/**
 * @Description: 定界
 * @author Raxcl
 * @date 2022-01-07 19:30:40
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
    public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer) {
        if (opener.length() >= CommonConstant.TWO && closer.length() >= CommonConstant.TWO) {
            // Use exactly two delimiters even if we have more, and don't care about internal openers/closers.
            return 2;
        } else {
            return 0;
        }
    }

    @Override
    public void process(Text opener, Text closer, int delimiterCount) {
        // Wrap nodes between delimiters in heimu.
        Node heimu = new Curtain();

        Node tmp = opener.getNext();
        while (tmp != null && tmp != closer) {
            Node next = tmp.getNext();
            heimu.appendChild(tmp);
            tmp = next;
        }

        opener.insertAfter(heimu);
    }
}
