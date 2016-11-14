// Cookbook Example Script for Clarity LIMS

import test_api.GLSRestApiUtils
import groovy.xml.StreamingMarkupBuilder
import java.text.SimpleDateFormat

/**
 * Description:
 *
 * This example shows how to post a sample to the api.
 */

className = getClass().simpleName
def cli = new CliBuilder(usage: "groovy ${className}.groovy -h HOSTNAME -u USERNAME -p PASSWORD -pj PROJECTNAME -c CONTAINERNAME -s SAMPLENAME")
cli.h(argName:'hostname',      longOpt:'hostname',      required:true, args:1, 'Server Ip and Port (Required)')
cli.u(argName:'username',      longOpt:'username',      required:true, args:1, 'LIMS username (Required)')
cli.p(argName:'password',      longOpt:'password',      required:true, args:1, 'LIMS password (Required)')
cli.pj(argName:'projectName',  longOpt:'projectName',   required:true, args:1, 'A unique name for the created project (Required)')
cli.c(argName:'containerName', longOpt:'containerName', required:true, args:1, 'A unique name for the created container (Required)')
cli.s(argName:'sampleName',    longOpt:'sampleName',    required:true, args:1, 'A unique name for the created sample (Required)')

def opt = cli.parse(args)
if (!opt) {
    System.exit(-1)
}

hostname = opt.h
username = opt.u
password = opt.p
projectName = opt.pj
containerName = opt.c
sampleName = opt.s

// Determine the URIs of the projects list and a researcher
projectListURI = "http://${hostname}/api/v2/projects"
researcherURI = "http://${hostname}/api/v2/researchers/1"

def builder = new StreamingMarkupBuilder()
builder.encoding = "UTF-8"
String openDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date())

// Create a new project using the Markup Builder
def projectDoc = builder.bind {
    mkp.xmlDeclaration()
    mkp.declareNamespace(prj: 'http://genologics.com/ri/project')
    'prj:project' {
        'name'("${projectName}")
        'open-date'("${openDate}")
        'researcher'(uri:researcherURI)
    }
}
// Post the new project to the API
createdProjectNode = GLSRestApiUtils.xmlStringToNode(projectDoc.toString())
createdProjectNode = GLSRestApiUtils.httpPOST(createdProjectNode, "${projectListURI}", username, password)
println GLSRestApiUtils.nodeToXmlString(createdProjectNode)

// Determine the URI of the containers list
containersListURI = "http://${hostname}/api/v2/containers"
// Create a new container using the Markup Builder
def containerDoc = builder.bind {
    mkp.xmlDeclaration()
    mkp.declareNamespace(con: 'http://genologics.com/ri/container')
    mkp.declareNamespace(udf: 'http://genologics.com/ri/userdefined')
    'con:container' {
        'name'("${containerName}")
        'type'(uri:"http://${hostname}/api/v2/containertypes/1", name:"96 well plate")
    }
}
// Post the new container to the API
createdContainerNode = GLSRestApiUtils.xmlStringToNode(containerDoc.toString())
println GLSRestApiUtils.nodeToXmlString(createdContainerNode)
createdContainerNode = GLSRestApiUtils.httpPOST(createdContainerNode, containersListURI, username, password)
println GLSRestApiUtils.nodeToXmlString(createdContainerNode)

// Determine the samples list URI
sampleListURI = "http://${hostname}/api/v2/samples"
// Create a sample using the Markup Builder
def sampleDoc = builder.bind {
  mkp.xmlDeclaration()
  mkp.declareNamespace(smp: 'http://genologics.com/ri/sample')
  'smp:samplecreation' {
    'name'("${sampleName}")
    'project'(uri:"${createdProjectNode.'@uri'}")
    'location' {
        'container'(limsid:"${createdContainerNode.'@limsid'}", uri:"${createdContainerNode.'@uri'}")
        'value'('A:1')
    }
  }
}
// Post the sample to the API
createdSampleNode = GLSRestApiUtils.xmlStringToNode(sampleDoc.toString())
createdSampleNode = GLSRestApiUtils.httpPOST(createdSampleNode, "${sampleListURI}", username, password)
println GLSRestApiUtils.nodeToXmlString(createdSampleNode)

