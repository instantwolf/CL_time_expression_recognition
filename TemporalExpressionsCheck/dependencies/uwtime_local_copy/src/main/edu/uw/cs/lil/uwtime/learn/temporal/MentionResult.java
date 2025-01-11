package edu.uw.cs.lil.uwtime.learn.temporal;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IDerivation;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.eval.entities.TemporalEntity;
import edu.uw.cs.lil.uwtime.parsing.TemporalContext;
import edu.uw.cs.lil.uwtime.utils.FormattingUtils;

public class MentionResult implements java.io.Serializable {
	private static String							INDENTATION			= "\t\t";
	private static final long						serialVersionUID	= -5859852309843402300L;
	private final TemporalJointChunk				chunk;
	private final TemporalContext					context;
	private final IDerivation<LogicalExpression>	derivation;
	private double									detectionProbability;
	private final TemporalEntity					entity;
	private final TemporalExecutionHistory			executionHistory;
	private double									resolutionProbability;
	private final String							type, value, mod;

	// For gold results with latent variables
	public MentionResult(TemporalJointChunk chunk, String type, String value,
			String mod) {
		this(chunk, type, value, mod, null, new TemporalContext(),
				new TemporalExecutionHistory(), null);
	}

	// Main constructor
	public MentionResult(TemporalJointChunk chunk, String type, String value,
			String mod, IDerivation<LogicalExpression> derivation,
			TemporalContext context, TemporalExecutionHistory executionHistory,
			TemporalEntity entity) {
		this.chunk = chunk;
		this.derivation = derivation;
		this.context = context;
		this.executionHistory = executionHistory;
		this.entity = entity;
		this.value = value;
		this.type = type;
		this.mod = mod;
	}

	// Temporary and incomplete results for detection
	public MentionResult(TemporalJointChunk chunk, TemporalEntity entity) {
		this(chunk, null, null, null, null, new TemporalContext(),
				new TemporalExecutionHistory(), entity);
	}

	public String beginAnnotation(String tid) {
		return String.format("<TIMEX3 %s %s %s%s>", "tid=\"" + tid + "\"",
				type == null ? "DATE" : "type=\"" + type + "\"", "value=\""
						+ value + "\"", mod == null ? "" : " mod=\"" + mod
						+ "\"");
	}

	public String endAnnotation() {
		return "</TIMEX3>";
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MentionResult)) {
			return false;
		}
		return this.getValue().equals(((MentionResult) other).getValue());
	}

	public TemporalJointChunk getChunk() {
		return chunk;
	}

	public TemporalContext getContext() {
		return context;
	}

	public IDerivation<LogicalExpression> getDerivation() {
		return derivation;
	}

	public double getDetectionProbability() {
		return detectionProbability;
	}

	public TemporalEntity getEntity() {
		return entity;
	}

	public TemporalExecutionHistory getExecutionHistory() {
		return executionHistory;
	}

	public String getMod() {
		return mod;
	}

	public double getResolutionProbability() {
		return resolutionProbability;
	}

	public LogicalExpression getSemantics() {
		return derivation == null ? null : derivation.getSemantics();
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return this.getValue().hashCode();
	}

	public void setDetectionProbability(double detectionProbability) {
		this.detectionProbability = detectionProbability;
	}

	public void setResolutionProbability(double resolutionProbability) {
		this.resolutionProbability = resolutionProbability;
	}

	@Override
	public String toString() {
		String s = "\n";
		s += FormattingUtils.formatContents(INDENTATION, "Value", value);
		s += FormattingUtils.formatContents(INDENTATION, "Type", type);
		if (mod != null) {
			s += FormattingUtils.formatContents(INDENTATION, "Mod", mod);
		}
		if (getSemantics() != null) {
			s += FormattingUtils.formatContents(INDENTATION, "Semantics",
					getSemantics());
		}
		if (context != null) {
			s += FormattingUtils
					.formatContents(INDENTATION, "Context", context);
		}
		return s;
	}
}