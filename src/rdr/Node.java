package rdr;

import java.util.*;

public class Node {

	public static class Condition {
		public final String attribute;
		public final String value;

		public Condition(String attribute, String value) {
			this.attribute = attribute;
			this.value = value;
		}
	}

	public final Optional<Condition> condition;
	public final String conclusion;
	public final Optional<Node> elseNode;
	public final Optional<Node> exceptNode;

	public Node(String conclusion) {
		this.conclusion = conclusion;
		this.condition = Optional.empty();
		this.elseNode = Optional.empty();
		this.exceptNode = Optional.empty();
	}

	public Node(String attribute, String value, String conclusion) {
		this(Optional.of(new Condition(attribute, value)), conclusion);
	}

	public Node(Optional<Condition> condition, String conclusion) {
		this.condition = condition;
		this.conclusion = conclusion;
		this.elseNode = Optional.empty();
		this.exceptNode = Optional.empty();
	}

	public Node(String attribute, String value, String conclusion, Optional<Node> elseNode, Optional<Node> exceptNode) {
		this(Optional.of(new Condition(attribute, value)), conclusion, elseNode, exceptNode);
	}

	public Node(Optional<Condition> condition, String conclusion, Optional<Node> elseNode, Optional<Node> exceptNode) {
		this.condition = condition;
		this.conclusion = conclusion;
		this.elseNode = elseNode;
		this.exceptNode = exceptNode;
	}

	public Node withElse(Node n) {
		return new Node(condition, conclusion, Optional.of(n), exceptNode);
	}

	public Node withExcept(Node n) {
		return new Node(condition, conclusion, elseNode, Optional.of(n));
	}

}
