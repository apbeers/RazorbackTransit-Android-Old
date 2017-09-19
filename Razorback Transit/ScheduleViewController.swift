//
//  ScheduleWebViewController.swift
//  Razorback Transit
//
//  Created by Andrew Beers on 9/13/17.
//  Copyright © 2017 Andrew Beers. All rights reserved.
//

import UIKit
import WebKit

class ScheduleViewController: BaseViewController, WKUIDelegate, UIScrollViewDelegate {

    @IBOutlet weak var ScheduleWebView: UIView!

    var mapName: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        webView = WKWebView(frame: ScheduleWebView.bounds, configuration: WKWebViewConfiguration())
        webView.scrollView.delegate = self
        webView.navigationDelegate = self
        
        ScheduleWebView.addSubview(webView)
        
        guard let pdf = Bundle.main.url(forResource: mapName, withExtension: "pdf") else {
            return
        }
        
        let request = URLRequest(url: pdf)
        webView.load(request)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func viewForZooming(in scrollView: UIScrollView) -> UIView? {
        return nil
    }
}