package com.example.demo.service;

import com.example.demo.dto.CampaignDTO;
import java.util.List;

public interface CampaignService {
    List<CampaignDTO> getActiveCampaigns();
    CampaignDTO createCampaign(CampaignDTO campaignDTO);
}