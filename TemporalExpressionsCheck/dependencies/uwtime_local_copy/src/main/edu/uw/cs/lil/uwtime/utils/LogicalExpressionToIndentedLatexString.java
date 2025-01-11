package edu.uw.cs.lil.uwtime.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jregex.Pattern;
import jregex.Replacer;
import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.lambda.printers.ILogicalExpressionPrinter;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.ILogicalExpressionVisitor;
import edu.uw.cs.utils.string.StringUtils;

/**
 * Produce an indented LATEX-formated string representing the logical form.
 *
 * @author Kenton Lee, Yoav Artzi
 */
public class LogicalExpressionToIndentedLatexString implements
		ILogicalExpressionVisitor {
	private final static String						DEFAULT_IDENT			= "~~";
	private final static String						NEWLINE					= "\\\\";
	private static final Replacer					SPECIAL_CHARS_REPLACER	= new Pattern(
																					"([\\{\\}_^$])")
																					.replacer("\\\\$1");
	private static final String[]					VARIABLE_NAMES;
	private int										currentDepth			= 0;
	private final String							indentation;
	private final Map<LogicalExpression, String>	mapping;

	private final StringBuilder						outputString			= new StringBuilder();

	private int										variableNameIndex		= 0;
	private int										variableSuffix			= 0;

	private LogicalExpressionToIndentedLatexString(
			Map<LogicalExpression, String> baseMapping, String indentation) {
		this.mapping = new HashMap<>(baseMapping);
		this.indentation = indentation;
	}

	static {
		final List<String> names = new ArrayList<>();
		for (char c = 'z'; c >= 'a'; --c) {
			names.add(Character.toString(c));

		}
		VARIABLE_NAMES = names.toArray(new String[0]);

	}

	public static String of(LogicalExpression expression,
			Map<LogicalExpression, String> baseMapping) {
		return of(expression, baseMapping, DEFAULT_IDENT);
	}

	public static String of(LogicalExpression exp,
			Map<LogicalExpression, String> baseMapping, String indentation) {
		final LogicalExpressionToIndentedLatexString visitor = new LogicalExpressionToIndentedLatexString(
				baseMapping, indentation);
		visitor.visit(exp);
		System.out.println(visitor.outputString.toString());
		return visitor.outputString.toString();
	}

	/**
	 * Escape the necessary characters for a Latex string and wrap it \textit.
	 */
	private static String latexIt(String str) {
		return "\\textit{" + SPECIAL_CHARS_REPLACER.replace(str) + "}";
	}

	@Override
	public void visit(Lambda lambda) {
		outputString.append("\\lambda ");
		lambda.getArgument().accept(this);
		outputString.append(". ");
		lambda.getBody().accept(this);
	}

	@Override
	public void visit(Literal literal) {
		if (LogicLanguageServices.isCoordinationPredicate(literal
				.getPredicate())) {
			// Case coordination predicate.
			final Iterator<LogicalExpression> iterator = literal.getArguments()
					.iterator();
			while (iterator.hasNext()) {
				iterator.next().accept(this);
				if (iterator.hasNext()) {
					outputString.append(' ');
					literal.getPredicate().accept(this);
					outputString.append(' ');
				}
			}
		} else {
			literal.getPredicate().accept(this);
			outputString.append('(');
			final Iterator<LogicalExpression> iterator = literal.getArguments()
					.iterator();
			final boolean doNewLine = literal.getArguments().size() > 1;

			if (doNewLine) {
				++currentDepth;
			}
			while (iterator.hasNext()) {
				if (doNewLine) {
					outputString.append(NEWLINE
							+ StringUtils.multiply(indentation, currentDepth));
				}
				iterator.next().accept(this);
				if (iterator.hasNext()) {
					outputString.append(", ");
				}
			}
			if (doNewLine) {
				--currentDepth;
			}
			outputString.append(')');
		}
	}

	@Override
	public void visit(LogicalConstant logicalConstant) {
		if (mapping.containsKey(logicalConstant)) {
			outputString.append(mapping.get(logicalConstant));
		} else if (logicalConstant.getType().isComplex()) {
			outputString.append(latexIt(logicalConstant.getBaseName()));
		} else {
			outputString.append(latexIt(logicalConstant.getBaseName()
					.toUpperCase()));
		}
	}

	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}

	@Override
	public void visit(Variable variable) {
		if (!mapping.containsKey(variable)) {
			if (variableNameIndex >= VARIABLE_NAMES.length) {
				++variableSuffix;
				variableNameIndex = 0;
			}
			mapping.put(
					variable,
					VARIABLE_NAMES[variableNameIndex++]
							+ (variableSuffix == 0 ? "" : Integer
									.valueOf(variableSuffix)));
		}
		outputString.append(mapping.get(variable));
	}

	public static class Printer implements ILogicalExpressionPrinter {

		private final Map<LogicalExpression, String>	baseMapping;

		public Printer(Map<LogicalExpression, String> baseMapping) {
			this.baseMapping = baseMapping;
		}

		@Override
		public String toString(LogicalExpression exp) {
			return of(exp, baseMapping);
		}

		public static class Builder {
			private final Map<LogicalExpression, String>	baseMapping	= new HashMap<>();

			public Builder addMapping(LogicalExpression exp, String string) {
				baseMapping.put(exp, string);
				return this;
			}

			public Printer build() {
				return new Printer(baseMapping);
			}

		}

	}

}
