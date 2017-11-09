package pattern.Builder;

public class NotOrientedERBuilder extends ModelBuilder {

	private ERModel model;

	public NotOrientedERBuilder() {         model = new ERModel();     }

	public void addEntity(String name) {
		model.addEntity(name);
	}

	public void addRelationship(String fromEntity, String toEntity, String relation) {
		model.addRelationship(fromEntity, toEntity, relation);
	}

	public void addCardMin(String entity, String relation, String value) {
		model.addCardMin(entity, relation, value);
	}

	public void addCardMax(String entity, String relation, String value) {
		model.addCardMax(entity, relation, value);
	}

	public Object getModel() {
		return model;
	}
}
