package pattern.Builder;

public class BuilderExample {
	public static void main(String[] arg) {
		String swIngCourseModel = (String) ERHardCodedDirector.getModel(new OrientedERBuilder());
		System.out.println(swIngCourseModel);
		ERModel dbCourseModel = (ERModel) ERHardCodedDirector.getModel(new NotOrientedERBuilder());
		dbCourseModel.showStructure();
	}
}
