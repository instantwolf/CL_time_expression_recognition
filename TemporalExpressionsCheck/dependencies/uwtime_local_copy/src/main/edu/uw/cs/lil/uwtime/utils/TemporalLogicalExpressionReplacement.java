package edu.uw.cs.lil.uwtime.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.ILogicalExpressionVisitor;
import edu.uw.cs.lil.uwtime.eval.entities.TemporalSequence;
import edu.uw.cs.lil.uwtime.parsing.TemporalContext;
import edu.uw.cs.utils.collections.ListUtils;

public class TemporalLogicalExpressionReplacement implements
		ILogicalExpressionVisitor {
	private final static LogicalExpressionCategoryServices			categoryServices;
	private final static Map<LogicalConstant, LogicalExpression>	constantsMap;
	private final static Set<LogicalConstant>						idConstants;
	private final static LogicalConstant							refConstant;
	private final static LogicalConstant							shift2Constant;
	private final static LogicalConstant							shift3Constant;
	private final TemporalContext									context;
	private LogicalExpression										result;

	protected TemporalLogicalExpressionReplacement(TemporalContext context) {
		this.context = context;
	}

	static {
		constantsMap = new HashMap<>();
		constantsMap.put(LogicalConstant.read("preceding:<s,<r,r>>"),
				LogicalConstant.read("nearest_backward:<s,<r,r>>"));
		constantsMap.put(LogicalConstant.read("following:<s,<r,r>>"),
				LogicalConstant.read("nearest_forward:<s,<r,r>>"));
		constantsMap.put(LogicalConstant.read("following:<s,<r,r>>"),
				LogicalConstant.read("nearest_forward:<s,<r,r>>"));
		refConstant = LogicalConstant.read("ref_time:r");
		shift2Constant = LogicalConstant.read("shift:<s,<d,s>>");
		shift3Constant = LogicalConstant.read("shift:<s,<d,<d,s>>>");
		idConstants = new HashSet<>();
		idConstants.add(LogicalConstant.read("id:<s,s>"));
		idConstants.add(LogicalConstant.read("id:<r,r>"));
		idConstants.add(LogicalConstant.read("id:<d,d>"));
		idConstants.add(LogicalConstant.read("id:<a,a>"));
		categoryServices = new LogicalExpressionCategoryServices(true);
	}

	public static LogicalExpression of(LogicalExpression expression,
			TemporalContext context) {
		final TemporalLogicalExpressionReplacement visitor = new TemporalLogicalExpressionReplacement(
				context);
		visitor.visit(expression);
		return visitor.result;
	}

	@Override
	final public void visit(Lambda lambda) {
		// No lambdas
	}

	@Override
	public void visit(Literal literal) {
		if (literal.getPredicate().equals(shift2Constant)
				&& literal.getArguments().size() < 3) {
			new Literal(
					shift3Constant,
					ListUtils.concat(
							literal.getArguments(),
							ListUtils
									.<LogicalExpression> createSingletonList(LogicalConstant
											.read(context.getGranularity() == TemporalContext.ShiftGranularity.DELTA ? "delta:d"
													: "origin:d"))))
					.accept(this);
		}
		if (idConstants.contains(literal.getPredicate())
				&& literal.getArguments().size() == 1) {
			literal.getArguments().get(0).accept(this);
		} else {
			literal.getPredicate().accept(this);
			final LogicalExpression visitedPredicate = result;
			final List<LogicalExpression> visitedArguments = new LinkedList<>();
			for (final LogicalExpression argument : literal.getArguments()) {
				argument.accept(this);
				visitedArguments.add(result);
			}
			result = new Literal(visitedPredicate, visitedArguments);
		}
	}

	@Override
	public void visit(LogicalConstant logicalConstant) {
		if (logicalConstant.equals(refConstant)) {
			LogicalConstant.read(
					context.getReferenceType().toLowerCase() + ":r").accept(
					this);
		} else if (logicalConstant.getType() == TemporalSequence.LOGICAL_TYPE) {
			LogicalExpression.read(
					"(seq:<d,s> " + logicalConstant.getBaseName() + ":d)")
					.accept(this);
		} else if (constantsMap.containsKey(logicalConstant)) {
			constantsMap.get(logicalConstant).accept(this);
		} else {
			result = logicalConstant;
		}
	}

	@Override
	public void visit(LogicalExpression logicalExpression) {
		context.applyTemporalDirection(logicalExpression, categoryServices)
				.accept(this);
	}

	@Override
	public void visit(Variable variable) {
		// No free variables
	}
}
