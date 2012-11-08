package fr.dutra.confluence2wordpress.core.converter.processors;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.atlassian.confluence.renderer.PageContext;


public class HeadingsCollectorTest {

	@Test
	public void test() throws Exception {
		String xml = IOUtils.toString(this.getClass().getResourceAsStream("/headings.txt"), "UTF-8");
		HeadingsCollector c = new HeadingsCollector();
		PageContext pageContext = Mockito.mock(PageContext.class);
		Mockito.when(pageContext.getPageTitle()).thenReturn("Test");
		Mockito.when(pageContext.getOriginalContext()).thenReturn(pageContext);
		c.preProcess(xml, null, pageContext);
		List<Heading> headings = c.getHeadings();
		assertEquals(2, headings.size());
		Heading heading1 = headings.get(0);
		Heading heading2 = headings.get(1);
		assertEquals("1", heading1.getLabel());
		assertEquals("Test-1", heading1.getAnchor());
		List<Heading> children1 = heading1.getChildren();
		assertEquals(2, children1.size());
		Heading heading11 = children1.get(0);
		assertEquals("1.1", heading11.getLabel());
		assertEquals("2", heading2.getLabel());
	}
}
