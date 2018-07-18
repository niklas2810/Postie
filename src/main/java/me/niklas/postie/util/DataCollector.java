/*
 *
 *      Copyright 2018 Niklas Arndt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package me.niklas.postie.util;

import me.niklas.postie.core.Postie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Niklas on 15.07.2018 in postie
 */
public class DataCollector {

    private final Logger logger = LoggerFactory.getLogger("DataCollector"); //The logger object, used to log errors

    private final String uuid; //The ID of the user
    private File zipFile; //the file where the information is stored

    /**
     * Creates the directory and sets the user id.
     *
     * @param uuid The id of the {@link net.dv8tion.jda.core.entities.User}.
     */
    public DataCollector(String uuid) {
        this.uuid = uuid;

        try {
            new File("collections").mkdir();
        } catch (Exception e) {
            logger.error("An error occurred while creating the collections directory: ", e);
        }
    }

    /**
     * Creates the userdata file.
     */
    public void create() {
        try {
            zipFile = new File("collections" + File.separator + uuid + ".zip"); //Initialize zip file object
            ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(zipFile)); //Initialize output stream

            //Store all permission levels
            ZipEntry entry = new ZipEntry("permissions.json");
            stream.putNextEntry(entry);

            //Write from permissions manager
            String data = Postie.getInstance().getPermissionManager().getDataForUser(uuid).toString();
            stream.write(data.getBytes(), 0, data.getBytes().length);
            stream.closeEntry();

            //Store all data
            entry = new ZipEntry("data.json");
            stream.putNextEntry(entry);

            //Write from data manager
            data = Postie.getInstance().getDataManager().get(uuid).toString();
            stream.write(data.getBytes(), 0, data.getBytes().length);
            stream.closeEntry();

            stream.close();

        } catch (Exception e) {
            logger.error("An error occurred while collecting the data: ", e);
        }
    }

    /**
     * @return The zip file.
     */
    public File getZipFile() {
        return zipFile;
    }

    /**
     * Removes the zip {@link File} of the {@link net.dv8tion.jda.core.entities.User}.
     *
     * @return Whether the file has successfully been deleted. If not, there may be many possible cases:
     * - The file does not exist
     * - No permission to delete it
     * - Used by another process
     * - Other errors
     */
    public boolean remove() {
        try {
            return zipFile.delete();
        } catch (Exception e) {
            logger.error("An error ocurred while deleting an data .zip file: ", e);
        }
        return false;
    }
}
