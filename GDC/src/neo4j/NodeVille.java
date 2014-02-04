package neo4j;

public class NodeVille {
	private String name;
	private String dataType;
	private String propName;

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
@Override
public String toString() {
	// TODO Auto-generated method stub
	return "Ville";
}
}
