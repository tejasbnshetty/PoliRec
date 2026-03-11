package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

public class PolicyTests {
    private HelperPolicy helperPolicy;
    private Policy policyModel;
    private ArrayList<Policy> policyList;
    private PolicyAdapter policyAdapter;
    private UserSession userSessionAdmin;
    private UserSession userSessionRegular;

    @Before
    public void setUp() {
        helperPolicy = new HelperPolicy();
        policyModel = new Policy("001", "Test description");
        policyList = new ArrayList<>();
        policyList.add(new Policy("001", "First policy"));
        policyList.add(new Policy("002", "Second policy"));
        policyAdapter = new PolicyAdapter(policyList);
        userSessionAdmin = new UserSession();
        userSessionAdmin.setState(new AdminState());
        userSessionRegular = new UserSession();
        userSessionRegular.setState(new RegularUserState());
    }

    @Test
    public void testHelperPolicySettersGetters() {
        helperPolicy.setPolicy_no("123");
        helperPolicy.setPolicytxt("Sample policy text");
        assertEquals("123", helperPolicy.getPolicy_no());
        assertEquals("Sample policy text", helperPolicy.getPolicytxt());
    }

    @Test
    public void testPolicyModelGetters() {
        assertEquals("001", policyModel.getPolicyNumber());
        assertEquals("Test description", policyModel.getPolicyDescription());
    }

    @Test
    public void testPolicyAdapterItemCount() {
        assertEquals(2, policyAdapter.getItemCount());
    }

    @Test
    public void testPolicyAdapterUpdateList() {
        ArrayList<Policy> newList = new ArrayList<>();
        newList.add(new Policy("100", "New policy entry"));
        policyAdapter.updateList(newList);
        assertEquals(1, policyAdapter.getItemCount());
    }

    @Test
    public void testUserSessionStates() {
        assertTrue(userSessionAdmin.canAddPolicy());
        assertEquals("Admin", userSessionAdmin.getRoleName());
        assertFalse(userSessionRegular.canAddPolicy());
        assertEquals("User", userSessionRegular.getRoleName());
    }
}
