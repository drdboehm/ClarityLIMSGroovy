// Cookbook Example Script for Clarity LIMS
import test_api.GLSRestApiUtils
import groovy.xml.StreamingMarkupBuilder

/**
 * Description:
 *
 * This example shows how to post a project to the API.
 */

className = getClass().simpleName
def cli = new CliBuilder(usage: "groovy ${className}.groovy -h HOSTNAME -u USERNAME -p PASSWORD -pj PROJECTNAME")
cli.h(argName:'hostname',     longOpt:'hostname',    required:true, args:1, 'Server Ip and Port (Required)')
cli.u(argName:'username',     longOpt:'username',    required:true, args:1, 'LIMS username (Required)')
cli.p(argName:'password',     longOpt:'password',    required:true, args:1, 'LIMS password (Required)')
cli.pj(argName:'projectName', longOpt:'projectName', required:true, args:1, 'A unique name for the created project (Required)')

def opt = cli.parse(args)
if (!opt) {
    System.exit(-1)
}

hostname = opt.h
username = opt.u
password = opt.p
projectName = opt.pj

// Determine project list's URI
String projectsListURI = "http://${hostname}/api/v2/projects"

def builder = new StreamingMarkupBuilder()
builder.encoding = "UTF-8"
openDate = "2010-08-24"

// Build a new project using Markup Builder
def projectDoc = builder.bind {
    mkp.xmlDeclaration()
    mkp.declareNamespace(prj: 'http://genologics.com/ri/project')
    mkp.declareNamespace(udf: 'http://genologics.com/ri/userdefined')
    'prj:project'{
        'name'(projectName)
        'open-date'(openDate)
        'researcher'(uri:"http://${hostname}/api/v2/researchers/1")
        'udf:field'(name:"Objective", "To test httpPOST")
    }
}
// Turn the markup into a node and post it to the API
def projectNode = GLSRestApiUtils.xmlStringToNode(projectDoc.toString())
projectNode = GLSRestApiUtils.httpPOST(projectNode, projectsListURI, username, password)
println GLSRestApiUtils.nodeToXmlString(projectNode)

