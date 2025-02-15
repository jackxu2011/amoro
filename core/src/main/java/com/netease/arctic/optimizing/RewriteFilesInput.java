/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netease.arctic.optimizing;

import com.netease.arctic.data.DefaultKeyedFile;
import com.netease.arctic.data.PrimaryKeyedFile;
import com.netease.arctic.table.ArcticTable;
import com.netease.arctic.utils.ContentFiles;
import org.apache.iceberg.ContentFile;
import org.apache.iceberg.DataFile;
import org.apache.iceberg.DeleteFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RewriteFilesInput extends BaseOptimizingInput {
  private static final long serialVersionUID = -696610318564333923L;
  private final DataFile[] rewrittenDataFiles;
  private final DataFile[] rePosDeletedDataFiles;
  private final ContentFile<?>[] readOnlyDeleteFiles;
  private final ContentFile<?>[] rewrittenDeleteFiles;
  private final ArcticTable table;

  public RewriteFilesInput(
      DataFile[] rewrittenDataFiles,
      DataFile[] rePosDeletedDataFiles,
      ContentFile<?>[] readOnlyDeleteFiles,
      ContentFile<?>[] rewrittenDeleteFiles,
      ArcticTable table) {
    this.rewrittenDataFiles = rewrittenDataFiles;
    this.rePosDeletedDataFiles = rePosDeletedDataFiles;
    this.readOnlyDeleteFiles = readOnlyDeleteFiles;
    this.rewrittenDeleteFiles = rewrittenDeleteFiles;
    this.table = table;
  }

  public DataFile[] rewrittenDataFiles() {
    return rewrittenDataFiles;
  }

  public DataFile[] rePosDeletedDataFiles() {
    return rePosDeletedDataFiles;
  }

  public ContentFile<?>[] readOnlyDeleteFiles() {
    return readOnlyDeleteFiles;
  }

  public ContentFile<?>[] rewrittenDeleteFiles() {
    return rewrittenDeleteFiles;
  }

  public List<PrimaryKeyedFile> rewrittenDataFilesForMixed() {
    if (rewrittenDataFiles == null) {
      return null;
    }
    return Arrays.stream(rewrittenDataFiles)
        .map(s -> (DefaultKeyedFile) s)
        .collect(Collectors.toList());
  }

  public List<PrimaryKeyedFile> rePosDeletedDataFilesForMixed() {
    if (rePosDeletedDataFiles == null) {
      return null;
    }
    return Arrays.stream(rePosDeletedDataFiles)
        .map(s -> (DefaultKeyedFile) s)
        .collect(Collectors.toList());
  }

  public List<DeleteFile> positionDeleteForMixed() {
    return Arrays.stream(deleteFiles())
        .filter(s -> s instanceof DeleteFile)
        .map(ContentFiles::asDeleteFile)
        .collect(Collectors.toList());
  }

  public List<PrimaryKeyedFile> equalityDeleteForMixed() {
    return Arrays.stream(deleteFiles())
        .filter(s -> s instanceof DataFile)
        .map(s -> (PrimaryKeyedFile) s)
        .collect(Collectors.toList());
  }

  public ContentFile<?>[] readOnlyDeleteFilesForMixed() {
    return readOnlyDeleteFiles;
  }

  public ContentFile<?>[] rewrittenDeleteFilesForMixed() {
    return rewrittenDeleteFiles;
  }

  public ContentFile<?>[] deleteFiles() {
    List<ContentFile<?>> list = new ArrayList<>();
    if (readOnlyDeleteFiles != null) {
      list.addAll(Arrays.asList(readOnlyDeleteFiles));
    }
    if (rewrittenDeleteFiles != null) {
      list.addAll(Arrays.asList(rewrittenDeleteFiles));
    }
    return list.toArray(new ContentFile<?>[0]);
  }

  public DataFile[] dataFiles() {
    List<DataFile> list = new ArrayList<>();
    if (rewrittenDataFiles != null) {
      list.addAll(Arrays.asList(rewrittenDataFiles));
    }
    if (rePosDeletedDataFiles != null) {
      list.addAll(Arrays.asList(rePosDeletedDataFiles));
    }
    return list.toArray(new DataFile[0]);
  }

  public ContentFile<?>[] allFiles() {
    List<ContentFile<?>> list = new ArrayList<>();
    if (rewrittenDataFiles != null) {
      list.addAll(Arrays.asList(rewrittenDataFiles));
    }
    if (rePosDeletedDataFiles != null) {
      list.addAll(Arrays.asList(rePosDeletedDataFiles));
    }
    if (readOnlyDeleteFiles != null) {
      list.addAll(Arrays.asList(readOnlyDeleteFiles));
    }
    if (rewrittenDeleteFiles != null) {
      list.addAll(Arrays.asList(rewrittenDeleteFiles));
    }
    return list.toArray(new ContentFile<?>[0]);
  }

  public ArcticTable getTable() {
    return table;
  }

  @Override
  public String toString() {
    return "RewriteFilesInput{"
        + "rewrittenDataFilesSize="
        + (rewrittenDataFiles == null ? 0 : rewrittenDataFiles.length)
        + ", rePosDeletedDataFilesSize="
        + (rePosDeletedDataFiles == null ? 0 : rePosDeletedDataFiles.length)
        + ", readOnlyDeleteFilesSize="
        + (readOnlyDeleteFiles == null ? 0 : readOnlyDeleteFiles.length)
        + ", rewrittenDeleteFilesSize="
        + (rewrittenDeleteFiles == null ? 0 : rewrittenDeleteFiles.length)
        + "} "
        + super.toString();
  }
}
