//
//  ViewController.swift
//  Razorback Transit
//
//  Created by Andrew Beers on 9/12/17.
//  Copyright © 2017 Andrew Beers. All rights reserved.
//

import UIKit
import WebKit

class LiveMapViewController: BaseViewController, WKUIDelegate, WKNavigationDelegate {

    @IBOutlet weak var LiveWebView: UIView!
    
    var webView: WKWebView!
    var defaults: UserDefaults!
    var needsUpdate = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        defaults = UserDefaults.standard
        NotificationCenter.default.addObserver(self, selector: #selector(self.checkIfNeedsReload), name: NSNotification.Name.UIApplicationWillEnterForeground, object: nil)
        
        webView = WKWebView(frame: LiveWebView.bounds, configuration: WKWebViewConfiguration())
        LiveWebView.addSubview(webView)
        webView.navigationDelegate = self
        
        guard let url = URL(string: "http://campusmaps.uark.edu/embed/routes") else {
            return
        }
        let request = URLRequest(url: url)
        webView.load(request)
        
        defaults.set(Date(), forKey: "date")
    }
    
    func checkIfNeedsReload() {
        
        defaults = UserDefaults.standard
        guard let lastLoaded = defaults.value(forKey: "date") as? Date else {
            return
        }
        
        guard let timeInterval = TimeInterval(exactly: -300) else {
            return
        }
        
        if lastLoaded.timeIntervalSince(Date()) < timeInterval && webView != nil {
            webView.reload()
        }
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}

