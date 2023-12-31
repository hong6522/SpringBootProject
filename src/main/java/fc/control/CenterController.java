package fc.control;


import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import fc.db.NoticeDTO;
import fc.db.NoticeMapper;
import fc.db.ProductDTO;
import fc.db.CenterPData;
import fc.db.MemberDTO;
import fc.db.MemberMapper;
import fc.db.QnaDTO;
import fc.db.QnaMapper;
import fc.db.ReviewDTO;
import fc.db.ReviewMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/center")
public class CenterController {
	
	@Resource
	NoticeMapper cm;
	
	@Resource
	QnaMapper qm;
	
	@Resource
	ReviewMapper rm;
	
	@Resource
	MemberMapper mp;
	
	@RequestMapping("/notice")
	String centerList(Model mm ,NoticeDTO dto,  HttpServletRequest request) {	
		//System.out.println(dto);
		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");
		int ntotal = cm.ntotalcount();
		  pd.setTotal(ntotal);
		List<NoticeDTO> mainData = cm.nlist(pd);
		 String sch = request.getParameter("sch");
		
		  pd.setTotal(ntotal);
		   // or "id" based on the selected option
		  if ("title".equals(dto.getKind())) {
		        pd.setKind("title");
		        pd.setSch(dto.getSch());
		        mainData = cm.nlist(pd);
		    } else if ("id".equals(dto.getKind())) {
		        pd.setKind("id");
		        pd.setSch(dto.getSch());
		        mainData = cm.nlist(pd);
		    } else {
		        mainData = cm.nlist(pd);
		    }
		   
		mm.addAttribute("pdata", pd);
		mm.addAttribute("mainData", mainData);
		return "center/notice";		
	}
	
	@RequestMapping("/noticeDetail/{no}/{nowpage}")
	String centerDetail(Model mm, NoticeDTO dto , @PathVariable int nowpage, HttpServletRequest request) {
		//System.out.println(dto.getId());
		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");
		int ntotal = cm.ntotalcount();
		  pd.setTotal(ntotal);
		  pd.setNowPage(nowpage);
		
		cm.ncnt(dto.no);
		
		mm.addAttribute("pdata", pd);
		mm.addAttribute("mainData", cm.ndetail(dto));
		
		
		return "center/noticeDetail";
	}
	
	@RequestMapping("/qna")
	String qnaList(Model mm ,QnaDTO dto , HttpServletRequest request ) {	
		String msg="";
		
		
		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");
		int qtotal = qm.qtotalcount();
		  pd.setTotal(qtotal);
		List<QnaDTO> mainData = qm.qlist(pd);
		
		//검색 종류
		  if ("title".equals(dto.getKind())) {
		        pd.setKind("title");
		        pd.setSch(dto.getSch());
		        mainData = qm.qlist(pd);
		    } else if ("id".equals(dto.getKind())) {
		        pd.setKind("id");
		        pd.setSch(dto.getSch());
		        mainData = qm.qlist(pd);
		    } else {
		        mainData = qm.qlist(pd);
		    }
		
		  
		mm.addAttribute("pdata", pd);
		mm.addAttribute("mainData", mainData);
		return "center/qna";		
	}
	
	
	@RequestMapping("/qnaDetail/{no}/{nowpage}")
	String qnaDetail(Model mm,@PathVariable int nowpage, QnaDTO dto , HttpServletRequest request ,HttpSession session) {
		
		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");
		qm.qcnt(dto.getNo());
		int qtotal = qm.qtotalcount();
		  pd.setTotal(qtotal);
	
		
		pd.setNowPage(nowpage);
		
		mm.addAttribute("pdata", pd);
		mm.addAttribute("mainData", qm.qdetail(dto));

		if(session.getAttribute("type")!=null) {
		
			mm.addAttribute("id", (String)session.getAttribute("id"));
			mm.addAttribute("mainData", qm.qdetail(dto));
			mm.addAttribute("pdata", pd);
			
			return  "center/qnaDetail";
		}
		else {
			return "center/qnaDetail";
		}
		
		
	}
	
	
	@GetMapping("/qnaInsert/{nowpage}")
	String qnaInsert(QnaDTO dto , Model mm,@PathVariable int nowpage,  HttpSession session  , HttpServletRequest request) {
	
						
		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");
		pd.setNowPage(nowpage);

		if(session.getAttribute("type")!=null) {

			mm.addAttribute("id", (String)session.getAttribute("id"));
			mm.addAttribute("mainData", qm.qdetail(dto));
			
			
			
			return  "center/qnaInsert";
		}
		
		
		mm.addAttribute("pdata", pd);
		mm.addAttribute("msg", "로그인이 필요한 서비스입니다. 회원으로 로그인 후 글 작성이 가능합니다.");
		mm.addAttribute("goUrl", "/fc_mem/login");
		
		
		return "center/alert";
	}
	
	
	@PostMapping("/qnaInsert/{nowpage}")
	String  qnaInsertComplete(Model mm , @PathVariable int nowpage, QnaDTO dto , HttpServletRequest request) {
		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");
		pd.setNowPage(nowpage);
		
		qm.qinsert(dto);
		
		//System.out.println(dto.no);
		
		mm.addAttribute("msg", "입력되었습니다.");
		mm.addAttribute("goUrl", "/center/qnaDetail/"+dto.getNo()+"/1");
		
		return "center/alert";
	}
	
	
	
	@GetMapping("/qnaModify/{no}/{nowpage}")
	String qnaModify(QnaDTO dto , Model mm ,@PathVariable int nowpage, HttpServletRequest request) {
		//String id = (String)session.getAttribute("id");
		
		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");
	
		int qtotal = qm.qtotalcount();
		  pd.setTotal(qtotal);
		  pd.setNowPage(nowpage);
		  
		  
		  
		System.out.println(qm.qdetail(dto));
		mm.addAttribute("mainData", qm.qdetail(dto));
		mm.addAttribute("pdata", pd);
	//	mm.addAttribute("id",id );

		return "center/qnaModify";
	}
	
	
	@PostMapping("/qnaModify/{no}/{nowpage}")
	String  qnaModifyComplete(Model mm, QnaDTO dto ,@PathVariable int nowpage ,HttpServletRequest request) {
	
		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");
		
		int qtotal = qm.qtotalcount();
		  pd.setTotal(qtotal);
		  
		  pd.setNowPage(nowpage);
		  
	    int cnt = qm.qmodify(dto);
	    
	    String msg = "수정 불가";   
		String goUrl = "/center/qnaModify/"+dto.getNo()+"/"+nowpage;
	   
	    
	    if (cnt > 0) {
	        msg = "수정되었습니다.";
	        goUrl = "/center/qnaDetail/"+dto.getNo()+"/"+nowpage;
	        mm.addAttribute("msg", msg);
	        mm.addAttribute("goUrl", goUrl);
	    }
	    mm.addAttribute("msg", msg);
        mm.addAttribute("goUrl", goUrl);
	   
	    
	    return "center/alert";
	}
	

	
	@RequestMapping(value = "qnaDelete/{no}", method = RequestMethod.GET )
	String qnaDelete(QnaDTO dto , Model mm) {
		 int cnt = qm.qdelete(dto.no);
		    String msg = "삭제 불가";
			String goUrl = "/center/qnaDelete/"+dto.getNo();
			
//				MemberDTO mDTO;
//			if(session.getAttribute("type")!=null) {
//				mDTO = new MemberDTO();
//				mDTO.setEmail((String)session.getAttribute("email"));
//				MemberDTO myPage = mp.myPage(mDTO);
//				mm.addAttribute("id", myPage.getId());
//				mm.addAttribute("mainData", rm.rdetail(dto));
//				mm.addAttribute("pdata", pd);
//				
//				return  "center/reviewDetail";
//			}
		   
		  System.out.println(dto.getId());
		  System.out.println(dto.getNo());
		    if (cnt > 0) {
		        msg = "삭제되었습니다.";
		        goUrl = "/center/qna";
		        mm.addAttribute("msg", msg);
		        mm.addAttribute("goUrl", goUrl);
		    }
		    mm.addAttribute("msg", msg);
	        mm.addAttribute("goUrl", goUrl);
		   
		    
		    return "center/alert";
	}
//	@GetMapping("/secretForm/{no}")
//	String secretFormInsert(QnaDTO dto , Model mm) {
//		//String id = (String)session.getAttribute("id");
//		
//		
//		mm.addAttribute("mainData", qm.qdetail(dto));
//		return "center/secretForm";
//	}
	
//	@PostMapping("/secretForm/{no}")
//	String secretFormComple(QnaDTO dto , Model mm) {
//		//String id = (String)session.getAttribute("id");
//		String msg = "암호인증실패";
//		String goUrl="/center/secretForm/"+dto.getNo();
//		
//		 int cnt = qm.pwchk(dto);
//		 System.out.println(cnt);
//		 if (cnt > 0) {
//		        msg = "암호인증성공";
//		        goUrl = "/center/qnaDetail/"+dto.getNo();
//		        mm.addAttribute("msg", msg);
//		        mm.addAttribute("goUrl", goUrl);
//		    }
//		 mm.addAttribute("msg", msg);
//	     mm.addAttribute("goUrl", goUrl);
//		 
//		return "center/alert";
//	}
	
	
	
			
	@RequestMapping("/review")
	String reviewList(Model mm ,ReviewDTO dto,  HttpServletRequest request) {	
		//System.out.println(dto);
		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");
		int rtotal = rm.rtotalcount();
		  pd.setTotal(rtotal);
		List<ReviewDTO> mainData = rm.rlist(pd);
		 String sch = request.getParameter("sch");
		
		
		  pd.setTotal(rtotal);
		   // or "id" based on the selected option
		  if ("title".equals(dto.getKind())) {
		        pd.setKind("title");
		        pd.setSch(dto.getSch());
		        mainData = rm.rlist(pd);
		    } else if ("id".equals(dto.getKind())) {
		        pd.setKind("id");
		        pd.setSch(dto.getSch());
		        mainData = rm.rlist(pd);
		    } else {
		        mainData = rm.rlist(pd);
		    }
		   
		mm.addAttribute("pdata", pd);
		mm.addAttribute("mainData", mainData);
		return "center/review";		
	}
	
	
	@RequestMapping("/reviewDetail/{no}/{nowpage}")
	String reviewDetail(Model mm , ReviewDTO dto ,@PathVariable int nowpage ,HttpServletRequest request , HttpSession session  ) {
		//String id = (String)session.getAttribute("type");

		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");
		rm.rcnt(dto.getNo());	
		

		pd.setNowPage(nowpage);
		pd.setNowPage(nowpage);
		if(session.getAttribute("type")!=null) {

			mm.addAttribute("id", (String)session.getAttribute("id"));
			mm.addAttribute("mainData", rm.rdetail(dto));
			mm.addAttribute("pdata", pd);
			
			return  "center/reviewDetail";
		}
		
		mm.addAttribute("pdata", pd);
		mm.addAttribute("mainData", rm.rdetail(dto));
		
	//System.out.println(mm.getAttribute("mainData"));
	
		
		return "center/reviewDetail";
	}
	
	@GetMapping("/reviewModify/{no}/{nowpage}")
	String reviewModify(ReviewDTO dto , Model mm , @PathVariable int nowpage ,HttpServletRequest request  ) {
		
		
		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");	
		int rtotal = rm.rtotalcount();
		pd.setTotal(rtotal);
		pd.setNowPage(nowpage);
		
		mm.addAttribute("reviewDTO", rm.rdetail(dto));
		System.out.println(mm.getAttribute("reviewDTO"));
//		mm.addAttribute("id",id );

		return "center/reviewModify";
	}


	@PostMapping("/reviewModify/{no}/{nowpage}")
	String  reviewModifyComplete(Model mm, ReviewDTO dto ,  MultipartFile ff1,MultipartFile ff2,MultipartFile ff3 , HttpServletRequest request , @PathVariable int nowpage ) {
	    	System.out.println("redto"+dto);
			new CenterPData(request);
			CenterPData pd = (CenterPData)request.getAttribute("pd");	
			int rtotal = rm.rtotalcount();
			pd.setTotal(rtotal);
			pd.setNowPage(nowpage);
			
			
			if(dto.getFf1()!=null) {
			dto.setUpfile(dto.getFf1().getOriginalFilename());
			}
			if(dto.getFf2()!=null) {
				dto.setUpfile1(dto.getFf2().getOriginalFilename());
			}
			if(dto.getFf3()!=null) {
				dto.setUpfile2(dto.getFf3().getOriginalFilename());
			}
			
			 int cnt = rm.rmodify(dto);
		    String msg = "수정 불가";   
			String goUrl = "/center/reviewModify/"+dto.getNo();
			
			if(cnt > 0) {		
				
			if(dto.getFf1()!=null) {
				fileSave(ff1,request);
				}
				if(dto.getFf2()!=null) {
				fileSave(ff2,request);
				}
				if(dto.getFf3()!=null) {
				fileSave(ff3,request);
				}
			  msg = "수정되었습니다.";
		      goUrl = "/center/reviewDetail/"+dto.getNo()+"/"+nowpage;
				
			}			      
	   
			
	    mm.addAttribute("msg", msg);
	    mm.addAttribute("goUrl", goUrl);
	   
	    
	    return "center/alert";
	}
	
	@RequestMapping(value = "/reviewDelete/{no}", method = RequestMethod.GET)
	public String myreviewDelete(@PathVariable("no") int no, Model model, HttpServletRequest request) {
	    ReviewDTO dto = rm.getreview(no);
	    int cnt = rm.rdelete(no);
	    String msg = "";
	    String goUrl = "";

	    if (dto != null) {
	        String fileName = dto.getUpfile();
	        String fileName1 = dto.getUpfile1();
	        String fileName2 = dto.getUpfile2();
	        if (fileName != null && !fileName.isEmpty()) {
	            boolean deleted = allFileDelete(fileName, request);
	            dto.setUpfile(""); // 파일명 초기화
	            if (deleted) {
	                msg = "삭제되었습니다.";
	                goUrl = "/center/review";
	            } else {
	                msg = "파일 삭제 실패";
	                goUrl = "/center/review/" + no;
	            }
	        }
	        
	        if (fileName1 != null && !fileName1.isEmpty()) {
	        	boolean    deleted = allFileDelete(fileName1, request);
	            dto.setUpfile1(""); // 파일명 초기화
	            if (deleted) {
	                msg = "삭제되었습니다.";
	                goUrl = "/center/review";
	            } else {
	                msg = "파일 삭제 실패";
	                goUrl = "/center/review/" + no;
	            }
	            
	            if (fileName2 != null && !fileName2.isEmpty()) {
	                 deleted = allFileDelete(fileName2, request);
	                dto.setUpfile2(""); // 파일명 초기화
	                if (deleted) {
	                    msg = "삭제되었습니다.";
	                    goUrl =  "/center/review";
	                } else {
	                    msg = "파일 삭제 실패";
	                    goUrl = "/center/review/" + no;
	                }
		                }
		            
		            
		        }
		    }
			
			    if (cnt > 0) {
			        msg = "삭제되었습니다.";
			        goUrl = "/center/review";
			    } else {
			        msg = "삭제 불가";
			        goUrl = "/center/review/" + no;
			    }

	    model.addAttribute("msg", msg);
	    model.addAttribute("goUrl", goUrl);

	    return "center/alert";
	}
	
	void fileSave(MultipartFile ff,	HttpServletRequest request) {
		String path = request.getServletContext().getRealPath("img");
		//System.out.println(path);
		try {
			FileOutputStream fos = new FileOutputStream(path+"\\"+ff.getOriginalFilename());
			fos.write(ff.getBytes());
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private boolean allFileDelete(String fileName, HttpServletRequest request) {
	    String path = request.getServletContext().getRealPath("img");
	    String filePath = path + "/" + fileName;
	    File file = new File(filePath);

	    if (file.exists()) {
	        if (file.delete()) {
	            System.out.println("파일이 삭제되었습니다.");
	            return true;
	        } else {
	            System.out.println("파일 삭제에 실패했습니다.");
	            return false;
	        }
	    } else {
	        System.out.println("파일이 존재하지 않습니다.");
	        return false;
	    }
	}


		
	@PostMapping("FileDelete/{fileName}/{nowpage}")
	String FileDelete(Model mm,ReviewDTO dto, @PathVariable String fileName , @PathVariable int nowpage , HttpServletRequest request) {
//		System.out.println("---************---"+fileName+"-----######------"+dto);\
		
		new CenterPData(request);
		CenterPData pd = (CenterPData)request.getAttribute("pd");
	
		int rtotal = rm.rtotalcount();
		  pd.setTotal(rtotal);
		  pd.setNowPage(nowpage);
		System.out.println("test");
		if(fileName.equals("upfile")) {
			dto.setUpfile(null);
			dto.setFf1(null);
		}
		if(fileName.equals("upfile1")) {
			dto.setUpfile1(null);
			dto.setFf2(null);
		}
		if(fileName.equals("upfile2")) {
			dto.setUpfile2(null);
			dto.setFf3(null);
		}
//		System.out.println(dto);
		int cnt = rm.rmodify(dto);
		mm.addAttribute("msg", "삭제완료");
		mm.addAttribute("goUrl", "../../reviewModify/"+dto.getNo()+"/"+pd.getNowPage());
		
		return "center/alert";			
	}
	
}