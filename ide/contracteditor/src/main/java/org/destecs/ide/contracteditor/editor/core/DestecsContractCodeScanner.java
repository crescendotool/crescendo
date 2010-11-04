package org.destecs.ide.contracteditor.editor.core;

import java.util.ArrayList;
import java.util.List;

import org.destecs.ide.contracteditor.editor.syntax.ContractColorProvider;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;


public class DestecsContractCodeScanner extends RuleBasedScanner
{
	public class ContractWordDetector implements IWordDetector {

		/*
		 * (non-Javadoc) Method declared on IWordDetector.
		 */
		public boolean isWordPart(char character) {
			return Character.isJavaIdentifierPart(character);
		}

		/*
		 * (non-Javadoc) Method declared on IWordDetector.
		 */
		public boolean isWordStart(char character) {
			return Character.isJavaIdentifierStart(character)
					|| isVdmIdentifierStart(character);
		}

		private boolean isVdmIdentifierStart(char character) {
			boolean isIdentifier = false;
			switch (character) {
			case '#':
			case '<':
				isIdentifier = true;
			
				
			default:

			}
			return isIdentifier;
		}

	}
	
	public class WhitespaceDetector implements IWhitespaceDetector {

		public boolean isWhitespace(char c) {

			return Character.isWhitespace(c);
		}

	}
	
	private String[] fgKeywords = getKeywords();
	private String[] contractKeywords = new String[] {};

	private String[] types = new String[] { "true", "false", "real", "bool" };

	public DestecsContractCodeScanner(ContractColorProvider provider)
	{

		// IToken comment = new Token(new TextAttribute(provider.getColor(VdmColorProvider.SINGLE_LINE_COMMENT)));
		//		
		// List<IRule> rules = new ArrayList<IRule>();
		// rules.addAll(Arrays.asList(fRules));
		//		
		//		
		// rules.add(new EndOfLineRule("#", comment));
		//		
		// IRule[] result = new IRule[rules.size()];
		// rules.toArray(result);
		// setRules(null);
		// setRules(result);

		IToken type = new Token(new TextAttribute(provider.getColor(new RGB(0, 0, 192)), null, SWT.BOLD));
		IToken keyword = new Token(new TextAttribute(provider.getColor(ContractColorProvider.KEYWORD), null, SWT.BOLD));
		IToken string = new Token(new TextAttribute(provider.getColor(ContractColorProvider.STRING)));
		IToken comment = new Token(new TextAttribute(provider.getColor(ContractColorProvider.SINGLE_LINE_COMMENT)));
		IToken other = new Token(new TextAttribute(provider.getColor(ContractColorProvider.DEFAULT)));

		List<IRule> rules = new ArrayList<IRule>();
		// Add rule for single line comments.
		rules.add(new EndOfLineRule("#", comment));
		// Multi line comment
		rules.add(new MultiLineRule("/*", "*/", comment));

		// Add rule for strings.
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		rules.add(new SingleLineRule("'", "'", string, '\\'));
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		// Add word rule for keywords.
		WordRule wordRule = new WordRule(new ContractWordDetector(), other);

		// TODO: this is a hack to get latex related stuff commented
		rules.add(new SingleLineRule("\\begin{vdm_al", "}", comment));
		rules.add(new SingleLineRule("\\end{vdm_al", "}", comment));

		for (int i = 0; i < fgKeywords.length; i++)
		{
			wordRule.addWord(fgKeywords[i], keyword);
		}

		for (int i = 0; i < types.length; i++)
		{
			wordRule.addWord(types[i], type);
		}

		wordRule.addWord("design_parameters", comment);
		wordRule.addWord("variables", comment);
		wordRule.addWord("events", comment);

		rules.add(wordRule);

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}

	protected String[] getKeywords()
	{
		// return new
		// String[]{"contract","end","variable","event","event_ree","event_fee","parameter","DT_OUT","CT_OUT"};
		return new String[] { "contract", "end", "design_parameter", "real",
				"monitored", "controlled", "event", "bool", "false", "true" };
	}

}
