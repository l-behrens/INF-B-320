package internetkaufhaus.model;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import internetkaufhaus.entities.ConcreteUserAccount;

public class CompetitionUnitTest {
	private ArrayList<ConcreteUserAccount> accs = new ArrayList<ConcreteUserAccount>();
	private Creditmanager credit;
	@Before
	public void init()
	{
		int i = 0;
		for(i=0;i<20;i++)
		{
			ConcreteUserAccount mockacc = mock(ConcreteUserAccount.class);
			when(mockacc.getCredits()).thenReturn(i);
			when(mockacc.getEmail()).thenReturn("kunde"+i+"@mock.de");
			accs.add(mockacc);
		}
		this.credit = mock(Creditmanager.class);
	}
	@Test
	public void getWinnersTest()
	{
		Competition com = new Competition(this.accs, this.credit);
		assertTrue("Es wurden "+com.getWinners().size()+" Gewinner ermittelt (falsch)", com.getWinners().size()==2);
	}
	@Test
	public void notifyWinnersTest()
	{
		ConcreteMailSender sender = mock(ConcreteMailSender.class);
		Competition com = new Competition(this.accs, this.credit);
		com.getWinners();
		com.notifyWinners(sender);
		verify(sender, times(2)).sendMail(anyString(),eq("Herzlichen Glückwunsch"),eq("wood@shop.de"),eq("Gewonnen"));
	}
}