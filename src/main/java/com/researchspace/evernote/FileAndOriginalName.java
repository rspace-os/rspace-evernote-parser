package com.researchspace.evernote;

import java.io.File;

import lombok.Value;

@Value
public class FileAndOriginalName {
  String originalName;
  File file;
}
