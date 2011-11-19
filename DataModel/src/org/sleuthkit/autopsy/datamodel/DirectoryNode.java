/*
 * Autopsy Forensic Browser
 *
 * Copyright 2011 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.datamodel;

import javax.swing.Action;
import org.sleuthkit.datamodel.Directory;
import org.sleuthkit.datamodel.TskData;

/**
 * This class is used to represent the "Node" for the directory.
 * Its children are more directories. 
 */
public class DirectoryNode extends AbstractFsContentNode<Directory> {

    /**
     * Helper so that the display name and the name used in building the path
     * are determined the same way.
     * @param d Directory to get the name of
     * @return short name for the directory
     */
    static String nameForDirectory(Directory d) {
        return d.getName();
    }

    /**
     * 
     * @param dir Underlying Content instance
     */
    public DirectoryNode(Directory dir) {
        super(dir);

        // set name, display name, and icon
        String dirName = nameForDirectory(dir);
        this.setDisplayName(dirName);
        if (Directory.dirFlagToValue(dir.getDir_flags()).equals(TskData.TSK_FS_NAME_FLAG_ENUM.TSK_FS_NAME_FLAG_UNALLOC.toString())) {
            this.setIconBaseWithExtension("org/sleuthkit/autopsy/images/folder-icon-deleted.png");
        } else {
            this.setIconBaseWithExtension("org/sleuthkit/autopsy/images/Folder-icon.png");
        }
    }

    /**
     * Right click action for this node
     *
     * @param popup
     * @return
     */
    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{};
    }

    @Override
    public Object[][] getRowValues(int rows) {

        // how many rows are we returning
        int maxRows = rows;
        if (this.getChildren().getNodesCount() < maxRows) {
            maxRows = this.getChildren().getNodesCount();
        }
        Object[][] objs = new Object[maxRows][];

        for (int i = 0; i < maxRows; i++) {
            PropertySet[] props = this.getChildren().getNodeAt(i).getPropertySets();
            Property[] property = props[0].getProperties();
            objs[i] = new Object[property.length - 1]; // - 1 because we don't want to show the location property

            // name property
            try {
                objs[i][0] = property[0].getValue();
            } catch (Exception ex) {
                objs[i][0] = "n/a";
            }

            // the rest of the properties(not including the location property)
            for (int j = 1; j < property.length - 1; j++) {
                try {
                    objs[i][j] = property[j + 1].getValue();
                } catch (Exception ex) {
                    objs[i][j] = "n/a";
                }
            }
        }
        return objs;
    }

    @Override
    public <T> T accept(ContentNodeVisitor<T> v) {
        return v.visit(this);
    }
}
