package ts.hn.tstest.dto;

public class DataDbDto {
    public String id, path, name, folder;
    public int type;
    private boolean isSelected = false;
    public String artist;

    public DataDbDto() {
        super();
    }

    public DataDbDto(String id, String path, int type, String name) {
        super();
        this.id = id;
        this.path = path;
        this.type = type;
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    
    
}
