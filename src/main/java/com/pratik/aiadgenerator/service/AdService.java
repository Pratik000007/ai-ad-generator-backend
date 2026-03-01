package com.pratik.aiadgenerator.service;

import com.pratik.aiadgenerator.dto.AdRequest;
import com.pratik.aiadgenerator.dto.AdResponse;
import com.pratik.aiadgenerator.entity.Ad;
import com.pratik.aiadgenerator.entity.Product;
import com.pratik.aiadgenerator.entity.User;
import com.pratik.aiadgenerator.repository.AdRepository;
import com.pratik.aiadgenerator.repository.ProductRepository;
import com.pratik.aiadgenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final ProductRepository productRepository;
    private final AdRepository adRepository;
    private final GeminiService geminiService;
    private final UserRepository userRepository;



    public List<Ad> getAdsByUser(String email) {
        return adRepository.findByUserEmail(email);
    }

   // public List<Ad> generateAds(Long productId) {
   public List<AdResponse> generateAds(Long productId, String email) {


       Product product = productRepository.findById(productId)
                .orElseThrow();

       User user = userRepository.findByEmail(email)
               .orElseThrow();

        String aiResponse = geminiService.generateAds(
                product.getName(),
                product.getDescription(),
                product.getTargetAudience()
        );

        System.out.println("===== RAW GEMINI RESPONSE =====");
        System.out.println(aiResponse);
        System.out.println("================================");


        // String text = aiResponse
        //        .split("\"text\":")[1]
         //       .replace("]}]}}", "")
         //       .replace("\"", "");


        JSONObject responseJson = new JSONObject(aiResponse);

        String text = responseJson
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");




        text = text
                .replace("```json", "")
                .replace("```", "")
                .trim();

        System.out.println("CLEAN JSON = " + text);
        JSONArray array = new JSONArray(text);

       // List<Ad> ads = new ArrayList<>();
       List<Ad> savedAds = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {


            JSONObject obj = array.getJSONObject(i);

            Ad ad = new Ad();
            ad.setHeadline(obj.getString("headline"));
            ad.setDescription(obj.getString("description"));
            ad.setCta(obj.getString("cta"));
            ad.setPlatform("Generic");
            ad.setProduct(product);

            // ✅ IMPORTANT — attach user
            ad.setUser(user);


            //ads.add(adRepository.save(ad));
            savedAds.add(adRepository.save(ad));
        }


       // 🔥 Generate ONE hero image
       String imageUrl = geminiService.generateAdImage(
               product.getName(),
               product.getDescription(),
               product.getTargetAudience()
       );



       //return ads;

       return savedAds.stream()
               .map(ad -> new AdResponse(
                       ad.getId(),
                       ad.getHeadline(),
                       ad.getDescription(),
                       ad.getCta(),
                       ad.getPlatform(),
                       imageUrl
               ))
               .toList();


    }

    // ✅ GET ADS BY PRODUCT (USED IN ProductController)
    //public List<Ad> getAdsByProduct(Long productId, String email) {

    public List<AdResponse> getAdsByProduct(Long productId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Product product = productRepository
                .findByIdAndUser(productId, user)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        //return adRepository.findByProduct(product);

        return adRepository.findByProduct(product)
                .stream()
                .map(ad -> new AdResponse(
                        ad.getId(),
                        ad.getHeadline(),
                        ad.getDescription(),
                        ad.getCta(),
                        ad.getPlatform(),
                        null

                ))
                .toList();

    }


    public List<AdResponse> generateFromRequest(AdRequest request,  String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        String aiResponse = geminiService.generateAds(
                request.getProduct(),
                request.getDescription(),
                request.getAudience()
        );

        JSONObject responseJson = new JSONObject(aiResponse);

        String text = responseJson
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");

        text = text
                .replace("```json", "")
                .replace("```", "")
                .trim();

        JSONArray array = new JSONArray(text);

        List<Ad> savedAds = new ArrayList<>();

        //List<AdResponse> responses = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {

            JSONObject obj = array.getJSONObject(i);


            //responses.add(new AdResponse(
             //       null,
             //       obj.getString("headline"),
              //      obj.getString("description"),
              //      obj.getString("cta"),
               //     "Generic"

            Ad ad = new Ad();
            ad.setHeadline(obj.getString("headline"));
            ad.setDescription(obj.getString("description"));
            ad.setCta(obj.getString("cta"));
            ad.setPlatform("Generic");

            // ✅ VERY IMPORTANT
            ad.setUser(user);

            savedAds.add(adRepository.save(ad));

           // ));
        }


        // 🔥 Generate hero image
        String imageUrl = geminiService.generateAdImage(
                request.getProduct(),
                request.getDescription(),
                request.getAudience()
        );


        // return responses;

        return savedAds.stream()
                .map(ad -> new AdResponse(
                        ad.getId(),
                        ad.getHeadline(),
                        ad.getDescription(),
                        ad.getCta(),
                        ad.getPlatform(),
                        imageUrl
                ))
                .toList();
    }



    public void deleteAd(Long id, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        // 🔐 SECURITY CHECK
        if (!ad.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this ad");
        }

        adRepository.delete(ad);
    }


}

