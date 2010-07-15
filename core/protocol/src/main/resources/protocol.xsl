<xsl:stylesheet version="1.0" xmlns:ddwrt="http://schemas.microsoft.com/WebParts/v2/DataView/runtime" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:msxsl="urn:schemas-microsoft-com:xslt" xmlns:ddwrt2="urn:frontpage:internal">

  <xsl:template match="/">
    <html>
      <head>
        <title>Protocol Definition</title>
        <link rel="stylesheet" href="protocol.css"/>
      </head>
      <body>
        <xsl:call-template name="topLevel"/>
      </body>
    </html>
  </xsl:template>


  <xsl:template name="topLevel">

    <h1>
      <xsl:value-of select="name(/*)"/>.<xsl:value-of select="XML-RPC_Library/name"/>
    </h1>
    <p>
    Interface Version: <span class="name"><xsl:value-of select="XML-RPC_Library/interfaceVersion/major"/>.<xsl:value-of select="XML-RPC_Library/interfaceVersion/minor"/>
    </span>
    <br/>
    Implementation Version: <span class="name"><xsl:value-of select="XML-RPC_Library/implementationVersion/major"/>.<xsl:value-of select="XML-RPC_Library/implementationVersion/minor"/>
    </span>
    </p>
    <p>
    Root name: <span class="name">
      <xsl:value-of select="name(/*)"/>
    </span>
    <br/>
    Group name: <span class="name"><xsl:value-of select="XML-RPC_Library/name"/></span>
    </p>
    <xsl:for-each select="XML-RPC_Library/methods">
      <xsl:call-template name="methods"/>
    </xsl:for-each>

    <xsl:for-each select="XML-RPC_Library/methodFaultResponses">
      <xsl:call-template name="methodFaultResponses"/>
    </xsl:for-each>
    
  </xsl:template>

  <xsl:template name="methods">
    <h2>Methods</h2>

    <xsl:for-each select="method">
      <xsl:call-template name="method">
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="method" match="method">

    <table class="method">
      <tr class="header">
        <th class="heading">Method:</th>
        <td>
          <xsl:value-of select="methodCall/methodName"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">
          <span class="description">
            <xsl:value-of select="description"/>
          </span>
        </td>
      </tr>
      <tr>
        <th class="heading">Parameters</th>
        <td>
          <xsl:for-each select="methodCall/params/param">
            <xsl:call-template name="param"/>
          </xsl:for-each>
        </td>
      </tr>
      <tr>
        <th class="heading">Returns</th>
        <td>
          <xsl:for-each select="methodResponse/params/param">
            <xsl:call-template name="param"/>
          </xsl:for-each>
        </td>
      </tr>


    </table>
    <p></p>
    <p></p>
  </xsl:template>


  <xsl:template name="param">
    <ul>
      <span class="description">
        <xsl:value-of select="description"/>
      </span>

      <xsl:apply-templates/>

    </ul>
  </xsl:template>

  <xsl:template match="value">
    <li/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="struct">
    <span class="typename">Struct:</span>
    <ul>
      <xsl:apply-templates/>
    </ul>

  </xsl:template>

  <xsl:template match="member">
    <li/>
      <xsl:for-each select="name">
        <xsl:call-template name="structname"/>
      </xsl:for-each>

      <xsl:for-each select="value">
        <xsl:call-template name="simplevalue"/>
      </xsl:for-each>

    <xsl:for-each select="description">
      <br/>
      <xsl:call-template name="description"/>
       
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="simplevalue">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template name="description" match="description">
    <span class ="description">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>

  <xsl:template match="array">
    <span class="typename">Array[] </span>
    <ol>
      <xsl:for-each select="data">
        <xsl:apply-templates/>
      </xsl:for-each>
    </ol>
  </xsl:template>


  <xsl:template match="string">
    <span class="typename">String </span>
    <span class ="description">
      <xsl:value-of select="."/>
    </span>

  </xsl:template>

  <xsl:template match="int">
    <span class="typename">Integer </span>
    <span class ="description">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>
  <xsl:template match="double">
    <span class="typename">Double </span>
    <span class ="description">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>
  <xsl:template match="boolean">
    <span class="typename">Boolean </span>
    <span class ="description">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>

  <xsl:template name="structname">
    <xsl:if test="* or text()">
      <span class="name"><xsl:value-of select="."/>: </span>
    </xsl:if>
    <xsl:if test="not (* or text())">
      <span class="missingname">No name given: </span>
      
    </xsl:if>
  </xsl:template>


  <xsl:template name="methodFaultResponses">
    <h2>Method Fault Responses</h2>
    <table class="fault">
      <tr class="header">
        <th class="heading">Fault Code</th>
        <th class="heading">Fault String</th>
      </tr>
    <xsl:for-each select="methodResponse">
      <xsl:call-template name="methodFaultResponse"/>
    </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template name="methodFaultResponse">
    <tr>
      <td>
        <xsl:value-of select="fault/value/struct/member[1]/value"/>
      </td>
      <td>
        <xsl:value-of select="fault/value/struct/member[2]/value"/>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>