/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.destecs.ide.core.utility;

import java.util.List;

public class SourceLocationConverter
{
	String content;
	CodeModel model;

	private static class CodeModel
	{
		private String[] codeLines;

		private int[] codeLineLengths;

		public CodeModel(String code)
		{
			init(code);
		}

		private void init(String code)
		{
			this.codeLines = code.split("\n");
	 		int count = this.codeLines.length;

			this.codeLineLengths = new int[count];

			int sum = 0;
			for (int i = 0; i < count; ++i)
			{
				this.codeLineLengths[i] = sum;
				sum += this.codeLines[i].length() + 1;
			}
		}

		public int[] getBounds(int lineNumber)
		{
			lineNumber -= 1;
			if (lineNumber > 0 && codeLines.length > lineNumber)
			{
				String codeLine = codeLines[lineNumber];
				int start = codeLineLengths[lineNumber];
				int end = start + codeLine.length();

				return new int[] { start, end };
			} else
				return new int[] { 0, 0 };
		}

		public int getLineCount()
		{
			return this.codeLines.length;
		}
	}

	public SourceLocationConverter(char[] content)
	{
		this.content = new String(content).replaceAll("\r\n", "\n");
		this.model = new CodeModel(this.content);
	}

	public SourceLocationConverter(List<Character> content)
	{
		this.content = new String(FileUtility.getCharContent(content));
		this.model = new CodeModel(this.content);
	}

	public int getStartPos(int line, int col)
	{
		return this.convert(line, col);
	}

	public int getEndPos(int line, int col)
	{
		return this.convert(line, col);

	}

	public int convert(int line, int offset)
	{
		int[] bounds = this.model.getBounds(line);
		return bounds[0] + offset;
	}

	public int getLineCount()
	{
		return this.model.getLineCount();
	}

	public int length()
	{
		return this.content.length();
	}
}
