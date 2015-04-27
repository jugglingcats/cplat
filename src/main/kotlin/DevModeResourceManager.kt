package com.akirkpatrick.cplat

import com.fasterxml.jackson.databind.ObjectMapper
import io.undertow.server.handlers.resource.*
import java.io.File
import java.util.LinkedHashMap

/**
 * @author alfie
 */
public class DevModeResourceManager(classLoader: ClassLoader) : ResourceManager {
    private var dependencies = LinkedHashMap<Any, List<Any>>()
    private val docroot: FileResourceManager
    private val project = FileResourceManager(File("."), 1000)
    private val classes: ClassPathResourceManager

    init {
        val base = File("src/main/webapp")
        if (!base.exists()) {
            System.out.println("Note: Running in dev mode but src/main/webapp does not exist (check working directory)")
        }
        this.docroot = FileResourceManager(base, 1000)
        this.classes = ClassPathResourceManager(classLoader)

        val objectMapper = ObjectMapper()
        val file = File("vendor.json")
        if (!file.exists()) {
            System.out.println("Note: Running in dev mode but cannot find vendor.json for dependency resolution")
        } else {
            dependencies = objectMapper.readValue<LinkedHashMap<Any, List<Any>>>(file, javaClass<LinkedHashMap<Any, List<Any>>>())
        }
    }

//    throws(javaClass<IOException>())
    override fun getResource(path: String): Resource? {
        // look in bower_components first
        var resource: Resource? = null
        resource = findDependencyResource(path, resource)
        resource = findDocrootResource(path, resource)

        if (resource == null) {
            resource = classes.getResource(path)
        }
        return resource
    }

    private fun findDocrootResource(path: String, resource: Resource?): Resource? {
        if (resource != null) {
            return resource
        }

        return docroot.getResource(path)
    }

    private fun findDependencyResource(path: String, resource: Resource?): Resource? {
        var mutablePath = path
        if (resource != null) {
            // already found
            return resource
        }
        while (mutablePath.startsWith("/")) {
            mutablePath = mutablePath.substring(1)
        }
        val ptr = mutablePath.indexOf('/') // determine prefix
        if (ptr <= 0) {
            return null
        }

        val prefix = mutablePath.substring(0, ptr)
        val filename = mutablePath.substring(ptr)

        val l = dependencies.get(prefix)
        if (l != null) {
            for (o in l) {
                val dep = o as String
                if (dep.endsWith(filename)) {
                    return project.getResource(dep)
                }
            }
        }
        return null
    }

    override fun isResourceChangeListenerSupported(): Boolean {
        return false
    }

    override fun registerResourceChangeListener(listener: ResourceChangeListener) {
        throw UnsupportedOperationException("Not supported")
    }

    override fun removeResourceChangeListener(listener: ResourceChangeListener) {
        throw UnsupportedOperationException("Not supported")
    }

    override fun close() {
        // do nothing
    }
}