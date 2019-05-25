package rdr;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

 public class Main {
	public static void main(String[] args) {
		InputStream inputStream = Main.class.getResourceAsStream("baeume.arff");
		//Classify trees with the names : Birke,Esche,Linde,Buche,Eiche
		double accuracy = computeAccuracy(inputStream);
		System.out.println("The accuracy : "+accuracy);
	}

	@SuppressWarnings("Duplicates")
	public static Optional<String> solve(Node n, Map<String, String> facts) {
		Optional<String> result = Optional.empty();
		if (n.condition.isEmpty()){
			result = Optional.of(n.conclusion);
			if (n.exceptNode.isPresent()){
				Optional<String> tmpResult = solve(n.exceptNode.get(),facts);
				if (tmpResult.isPresent()){
					result = tmpResult;
				}
			}
		}else{
			Node.Condition condition = n.condition.get();
			if (facts.containsKey(condition.attribute)&& condition.value.equals(facts.get(condition.attribute))){
				result = Optional.of(n.conclusion);
				if (n.exceptNode.isPresent()){
					Optional<String> tmpResult = solve(n.exceptNode.get(),facts);
					if (tmpResult.isPresent()){
						result = tmpResult;
					}
				}
			}else {
				if (n.elseNode.isPresent()){
					Optional<String> tmpResult = solve(n.elseNode.get(),facts);
					if (tmpResult.isPresent()){
						result = tmpResult;
					}
				}
			}
		}
		return result;
	}

	public static Node getRules() {

		final Node node = new Node("Birke")
				.withExcept(new Node("Blätter - Blattform", "rund oder eliptisch", "Eiche")
						.withExcept(new Node("Blätter - Blattanordnung", "gegenständig", "Esche")
							.withExcept(new Node("Stamm - Stammoberfläche", "tiefrissig", "Birke"))

						.withElse(new Node("Blätter - Blattanordnung", "wechselständig", "Eiche")
							.withExcept(new Node("Stamm - Stammoberfläche", "glatt", "Buche"))
								)
						)
				.withElse(new Node("Blätter - Blattform", "herzförmig", "Linde")))
				;
		return node;
	}
	
	public static double computeAccuracy(InputStream inputStream) {
		List<String> checklist = new ArrayList<>();
		checklist.add("Eiche");
		checklist.add("Buche");
		checklist.add("Linde");
		checklist.add("Esche");
		checklist.add("Birke");

		int trueResult = 0;
		int falseResult = 0;

		double result = 0.0;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		try {
			ArffLoader.ArffReader reader = new ArffLoader.ArffReader(bufferedReader);
			Instances instances = reader.getData();
			Iterator<Instance> iterator = instances.enumerateInstances().asIterator();
			Attribute attTreeName = instances.attribute("Baumgattung");
			while (iterator.hasNext()){
				Instance instance = iterator.next();
				if (checklist.contains(instance.stringValue(attTreeName))){
					System.out.println("Instance : ");
					System.out.println(instance);
					String treeName = instance.stringValue(attTreeName);
					Map<String, String> facts = new HashMap<>();
					instance.enumerateAttributes().asIterator().forEachRemaining(attribute -> {
						if (!attribute.isNumeric()){
							facts.put(attribute.name(),instance.stringValue(attribute));
						}
					});
					Optional<String> tmpResult = solve(getRules(),facts);
					System.out.println("Classified as : "+tmpResult+" value : "+treeName);
					if (tmpResult.isPresent()){
						if (tmpResult.get().equals(treeName)){
							trueResult++;
						}else {
							falseResult++;
						}
					}else {
							falseResult++;
					}
				}
			}
			result = (double) trueResult/(double) (falseResult+trueResult);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO
		return result;
	}
}
