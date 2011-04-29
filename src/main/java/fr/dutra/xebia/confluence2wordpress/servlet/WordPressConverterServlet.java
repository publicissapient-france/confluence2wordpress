package fr.dutra.xebia.confluence2wordpress.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.user.User;
import com.opensymphony.util.TextUtils;

public class WordPressConverterServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private PageManager pageManager;

    private WikiStyleRenderer wikiStyleRenderer;

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setWikiStyleRenderer( WikiStyleRenderer wikiStyleRenderer ) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException
    {
        User user = AuthenticatedUserThreadLocal.getUser();

        if( user == null ) {
            resp.sendRedirect( "/" );
            return;
        }

        //this will activate sitemesh decoration
        resp.setContentType("text/html;charset=UTF-8");

        PrintWriter out = resp.getWriter();

        out.println( "<html><head><title>WordPress Converter</title>" );
        out.println( "<meta name=\"decorator\" content=\"atl.general\"/>" );

        out.println( "<meta name=\"tab\" content=\"navigation\">" );
        out.println( "</head><body>" );

        out.println( "<pre>" );

        long pageId = Long.parseLong(req.getParameter( "pageId" ));

        Page page = pageManager.getPage(pageId);

        out.println( page.getContent() );

        out.println( "</pre> <hr />" );

        String html = wikiStyleRenderer.convertWikiToXHtml(  page.toPageContext(), page.getContent() );

        out.println(html);

        out.println( "<hr /> <pre>" );

        out.println(TextUtils.htmlEncode(html));

        out.println( "</pre>" );

        out.println( "</body></html>" );
        out.close();
    }

}