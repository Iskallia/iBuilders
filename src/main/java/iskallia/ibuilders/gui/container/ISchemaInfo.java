package iskallia.ibuilders.gui.container;

import iskallia.ibuilders.schematic.BuildersSchematic;

import java.util.List;

public interface ISchemaInfo {

    void setInfoList(List<BuildersSchematic.Info> infoList);

    List<BuildersSchematic.Info> getInfoList();

}
