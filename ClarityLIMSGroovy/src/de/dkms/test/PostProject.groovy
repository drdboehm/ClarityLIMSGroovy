<<<<<<< HEAD
// Cookbook Example Script for Clarity LIMS
import test_api.GLSRestApiUtils
import groovy.xml.StreamingMarkupBuilder

=======
package de.dkms.test
// Cookbook Example Script for Clarity LIMS
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.management.openmbean.OpenDataException;

import test_api.GLSRestApiUtils
import groovy.xml.StreamingMarkupBuilder


>>>>>>> 9a8790360d5066fb7a9afee9f61720c624094ea3
/**
 * Description:
 *
 * This example shows how to post a project to the API.
 */

className = getClass().simpleName
def cli = new CliBuilder(usage: "groovy ${className}.groovy -h HOSTNAME -u USERNAME -p PASSWORD -pj PROJECTNAME")
<<<<<<< HEAD
=======

>>>>>>> 9a8790360d5066fb7a9afee9f61720c624094ea3
cli.h(argName:'hostname',     longOpt:'hostname',    required:true, args:1, 'Server Ip and Port (Required)')
cli.u(argName:'username',     longOpt:'username',    required:true, args:1, 'LIMS username (Required)')
cli.p(argName:'password',     longOpt:'password',    required:true, args:1, 'LIMS password (Required)')
cli.pj(argName:'projectName', longOpt:'projectName', required:true, args:1, 'A unique name for the created project (Required)')

def opt = cli.parse(args)
if (!opt) {
<<<<<<< HEAD
    System.exit(-1)
=======
	System.exit(-1)
>>>>>>> 9a8790360d5066fb7a9afee9f61720c624094ea3
}

hostname = opt.h
username = opt.u
password = opt.p
projectName = opt.pj

<<<<<<< HEAD
// Determine project list's URI
String projectsListURI = "http://${hostname}/api/v2/projects"
ProxySetup.setProxy();
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
=======

// Determine project list's URI
String projectsListURI = "http://${hostname}/api/v2/projects"

// define the project XML using StreamingMarkupBuilder
def builder = new StreamingMarkupBuilder()
builder.encoding = "UTF-8"
// openDate = "2010-08-24"


formatter  = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
openDate = formatter.format(new Date())

println openDate


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
>>>>>>> 9a8790360d5066fb7a9afee9f61720c624094ea3
}
// Turn the markup into a node and post it to the API
def projectNode = GLSRestApiUtils.xmlStringToNode(projectDoc.toString())
projectNode = GLSRestApiUtils.httpPOST(projectNode, projectsListURI, username, password)
println GLSRestApiUtils.nodeToXmlString(projectNode)

