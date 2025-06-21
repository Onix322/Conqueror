package loader.utilities;

import loader.objects.Dependency;
import loader.objects.Exclusion;
import loader.objects.NodeAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Factory {

    private static class Holder{
        private static final Factory INSTANCE = new Factory();
    }

    public static Factory getInstance(){
        return Factory.Holder.INSTANCE;
    }

    public Dependency buildDependency(NodeAttributes rawDependency, List<Exclusion> exclusions){
        Map<String, String> attributes = rawDependency.getAttributes();

        if (attributes.get("groupId") == null || attributes.get("artifactId") == null) {
            throw new IllegalArgumentException("Dependency must have groupId and artifactId (mandatory)");
        }
        return Dependency.builder()
                .groupId(attributes.get("groupId"))
                .artifactId(attributes.get("artifactId"))
                .version(attributes.getOrDefault("version", null))
                .type(attributes.getOrDefault("type", null))
                .classifier(attributes.getOrDefault("classifier", null))
                .scope(attributes.getOrDefault("scope", null))
                .optional(Boolean.valueOf(attributes.getOrDefault("optional", null)))
                .exclusions(exclusions)
                .build();
    }

    public Exclusion buildExclusion(NodeAttributes nodeAttributes){
        Map<String, String> attributes = nodeAttributes.getAttributes();
        if (attributes.get("groupId") == null || attributes.get("artifactId") == null) {
            throw new IllegalArgumentException("Dependency must have groupId and artifactId (mandatory)");
        }
        return new Exclusion(
                attributes.get("groupId"),
                attributes.get("artifactId")
        );
    }

    public List<Dependency> buildDependencies(Map<NodeAttributes, List<NodeAttributes>> nodeAttributes){
        List<Dependency> dependencies = new ArrayList<>();
        nodeAttributes.forEach((key, value) -> {
            List<Exclusion> exclusions = new ArrayList<>();

            value.forEach(ex -> {
                Exclusion exclusion = this.buildExclusion(ex);
                exclusions.add(exclusion);
            });

            Dependency dependency = this.buildDependency(key, exclusions);
            dependencies.add(dependency);
        });
        return dependencies;
    }
}
