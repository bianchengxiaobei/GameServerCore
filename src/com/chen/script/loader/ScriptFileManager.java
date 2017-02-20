package com.chen.script.loader;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class ScriptFileManager implements JavaFileManager
{
	private JavaFileManager javaFileManager;
	private ClassLoader loader;
	public ScriptFileManager(JavaFileManager javaFileManager,ClassLoader loader)
	{
		this.javaFileManager = javaFileManager;
		this.loader = loader;
	}
	@Override
	public int isSupportedOption(String option) {
		return this.javaFileManager.isSupportedOption(option);
	}

	@Override
	public ClassLoader getClassLoader(Location location) {
		 return this.loader;
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName,
			Set<Kind> kinds, boolean recurse) throws IOException {
		return this.javaFileManager.list(location, packageName, kinds, recurse);
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		return this.javaFileManager.inferBinaryName(location, file);
	}

	@Override
	public boolean isSameFile(FileObject a, FileObject b) {
		return this.javaFileManager.isSameFile(a, b);
	}

	@Override
	public boolean handleOption(String current, Iterator<String> remaining) {
		 return this.javaFileManager.handleOption(current, remaining);
	}

	@Override
	public boolean hasLocation(Location location) {
		 return this.javaFileManager.hasLocation(location);
	}

	@Override
	public JavaFileObject getJavaFileForInput(Location location,
			String className, Kind kind) throws IOException {
		return this.javaFileManager.getJavaFileForInput(location, className, kind);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling) throws IOException {
		return this.javaFileManager.getJavaFileForOutput(location, className, kind, sibling);
	}

	@Override
	public FileObject getFileForInput(Location location, String packageName,
			String relativeName) throws IOException {
		return this.javaFileManager.getFileForInput(location, packageName, relativeName);
	}

	@Override
	public FileObject getFileForOutput(Location location, String packageName,
			String relativeName, FileObject sibling) throws IOException {
		return this.javaFileManager.getFileForOutput(location, packageName, relativeName, sibling);
	}

	@Override
	public void flush() throws IOException {
		this.javaFileManager.flush();
	}

	@Override
	public void close() throws IOException {
		this.javaFileManager.close();
	}
	
}
