'use client'

import { motion, Variants } from 'framer-motion'
import { ArrowRight, BarChart3, Clock, ChefHat, LayoutDashboard, Globe, Shield } from 'lucide-react'
import Link from 'next/link'
import { Button } from '@restaurantos/ui'

export default function LandingPage() {
    const features = [
        {
            icon: <LayoutDashboard className="w-6 h-6" />,
            title: "Smart Dashboard",
            description: "Get real-time insights into your restaurant's performance with interactive analytics."
        },
        {
            icon: <ChefHat className="w-6 h-6" />,
            title: "Kitchen Management",
            description: "Streamline orders and reduce waste with our intelligent kitchen display system."
        },
        {
            icon: <Clock className="w-6 h-6" />,
            title: "Real-time Updates",
            description: "Sync all your devices instantly. From table to kitchen, everything stays in perfect harmony."
        }
    ]

    const pricingPlans = [
        {
            name: "Starter",
            price: "$49",
            description: "Perfect for small cafes and food trucks.",
            features: ["Up to 5 staff members", "Basic analytics", "Mobile ordering", "Email support"],
            cta: "Start Free Trial",
            popular: false
        },
        {
            name: "Pro",
            price: "$99",
            description: "Ideal for growing full-service restaurants.",
            features: ["Unlimited staff members", "Advanced analytics", "Kitchen display system", "24/7 Priority support", "Inventory management"],
            cta: "Get Started",
            popular: true
        },
        {
            name: "Enterprise",
            price: "Custom",
            description: "For large chains and multi-location groups.",
            features: ["Multi-location management", "Custom integrations", "Dedicated account manager", "On-site training"],
            cta: "Contact Sales",
            popular: false
        }
    ]

    const containerVariants: Variants = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: {
                staggerChildren: 0.1
            }
        }
    }

    const itemVariants: Variants = {
        hidden: { opacity: 0, y: 20 },
        visible: { opacity: 1, y: 0 }
    }

    return (
        <div className="min-h-screen bg-slate-950 text-slate-50 selection:bg-orange-500/30 selection:text-orange-200 overflow-x-hidden">
            {/* Navbar */}
            <nav className="fixed top-0 w-full z-50 bg-slate-950/80 backdrop-blur-md border-b border-slate-800">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                        <motion.div
                            whileHover={{ rotate: 15 }}
                            className="w-8 h-8 bg-orange-600 rounded-lg flex items-center justify-center shadow-lg shadow-orange-600/20"
                        >
                            <ChefHat className="text-white w-5 h-5" />
                        </motion.div>
                        <span className="text-xl font-bold tracking-tight bg-gradient-to-r from-white to-slate-400 bg-clip-text text-transparent">RestaurantOS</span>
                    </div>
                    <div className="hidden md:flex items-center gap-8 text-sm font-medium text-slate-400">
                        <Link href="#features" className="hover:text-orange-500 transition-colors">Features</Link>
                        <Link href="#pricing" className="hover:text-orange-500 transition-colors">Pricing</Link>
                        <Link href="#about" className="hover:text-orange-500 transition-colors">About</Link>
                    </div>
                    <div className="flex items-center gap-4">
                        <Link href="/login">
                            <Button variant="ghost" className="text-slate-300 hover:text-white transition-all">Sign In</Button>
                        </Link>
                        <Link href="/register">
                            <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                <Button className="bg-orange-600 hover:bg-orange-700 text-white border-none shadow-lg shadow-orange-600/20">Get Started</Button>
                            </motion.div>
                        </Link>
                    </div>
                </div>
            </nav>

            <main>
                {/* Hero Section */}
                <section className="relative pt-32 pb-20 lg:pt-48 lg:pb-32">
                    {/* Background decoration */}
                    <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full h-[600px] bg-orange-500/10 blur-[120px] rounded-full" />
                    <div className="absolute top-1/4 -right-1/4 w-[500px] h-[500px] bg-blue-500/10 blur-[120px] rounded-full" />

                    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative">
                        <motion.div
                            initial={{ opacity: 0, y: 30 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ duration: 0.8, ease: "easeOut" }}
                            className="text-center"
                        >
                            <motion.div
                                initial={{ opacity: 0, scale: 0.8 }}
                                animate={{ opacity: 1, scale: 1 }}
                                transition={{ delay: 0.2 }}
                                className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-slate-900/50 border border-white/10 text-slate-400 text-xs font-semibold mb-6 backdrop-blur-sm"
                            >
                                <div className="w-2 h-2 rounded-full bg-orange-500 animate-pulse" />
                                V2.0 Now Available
                            </motion.div>
                            <h1 className="text-5xl lg:text-8xl font-black tracking-tighter mb-6 bg-gradient-to-b from-white via-white to-slate-500 bg-clip-text text-transparent leading-[1.1]">
                                Revolutionize Your <br />
                                <span className="bg-gradient-to-r from-orange-400 to-orange-600 bg-clip-text text-transparent">Kitchen & Service</span>
                            </h1>
                            <p className="max-w-2xl mx-auto text-lg lg:text-xl text-slate-400 mb-10 leading-relaxed font-medium">
                                The all-in-one OS for modern restaurateurs. Manage orders, team, and
                                analytics with a beautiful, intuitive interface.
                            </p>
                            <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
                                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                    <Button size="lg" className="h-14 px-10 bg-orange-600 hover:bg-orange-700 text-white text-lg font-bold border-none group shadow-xl shadow-orange-600/30">
                                        Start Free Trial
                                        <ArrowRight className="ml-2 w-5 h-5 group-hover:translate-x-1 transition-transform" />
                                    </Button>
                                </motion.div>
                                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                    <Button size="lg" variant="outline" className="h-14 px-10 border-white/10 bg-white/5 backdrop-blur-md hover:bg-white/10 text-slate-300 transition-all font-bold">
                                        Book a Demo
                                    </Button>
                                </motion.div>
                            </div>
                        </motion.div>

                        {/* Mockup / Visual */}
                        <motion.div
                            initial={{ opacity: 0, y: 100 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: 0.4, duration: 1 }}
                            className="mt-24 relative px-4"
                        >
                            <div className="relative mx-auto max-w-[1100px] aspect-[16/10] rounded-[2rem] border border-white/10 bg-slate-900/50 p-3 shadow-[0_0_100px_rgba(249,115,22,0.15)] overflow-hidden backdrop-blur-xl">
                                <div className="h-full w-full rounded-[1.5rem] bg-slate-950 overflow-hidden relative group border border-white/5">
                                    <div className="absolute inset-0 bg-gradient-to-tr from-orange-500/10 via-transparent to-blue-500/10" />
                                    <div className="h-full w-full p-8 flex flex-col gap-8 relative z-10">
                                        <div className="flex justify-between items-center">
                                            <div className="flex gap-2">
                                                <div className="w-3 h-3 rounded-full bg-red-500/50" />
                                                <div className="w-3 h-3 rounded-full bg-yellow-500/50" />
                                                <div className="w-3 h-3 rounded-full bg-green-500/50" />
                                            </div>
                                            <div className="h-8 w-32 bg-white/5 rounded-full border border-white/10" />
                                        </div>
                                        <div className="grid grid-cols-3 gap-6">
                                            {[
                                                { icon: <BarChart3 className="text-orange-500" /> },
                                                { icon: <ChefHat className="text-blue-500" /> },
                                                { icon: <Clock className="text-emerald-500" /> }
                                            ].map((item, i) => (
                                                <motion.div
                                                    key={i}
                                                    whileHover={{ y: -5 }}
                                                    className="h-40 rounded-2xl bg-white/5 border border-white/10 flex flex-col items-center justify-center gap-4 transition-colors hover:bg-white/[0.08]"
                                                >
                                                    <div className="p-3 rounded-xl bg-slate-950/50">
                                                        {item.icon}
                                                    </div>
                                                    <div className="h-2 w-20 bg-white/10 rounded-full" />
                                                    <div className="h-2 w-12 bg-white/5 rounded-full" />
                                                </motion.div>
                                            ))}
                                        </div>
                                        <div className="flex-1 rounded-2xl bg-white/5 border border-white/10 p-6 flex flex-col gap-6">
                                            <div className="flex justify-between">
                                                <div className="h-4 w-40 bg-white/10 rounded-full" />
                                                <div className="h-4 w-12 bg-white/10 rounded-full" />
                                            </div>
                                            <div className="space-y-6">
                                                {[1, 2].map(i => (
                                                    <div key={i} className="flex gap-4 items-center">
                                                        <div className="w-12 h-12 rounded-xl bg-white/10" />
                                                        <div className="flex-1 space-y-3">
                                                            <div className="h-2 w-full bg-white/10 rounded-full" />
                                                            <div className="h-2 w-2/3 bg-white/5 rounded-full" />
                                                        </div>
                                                        <div className="w-20 h-8 rounded-lg bg-orange-600/20 border border-orange-500/30" />
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </motion.div>
                    </div>
                </section>

                {/* Features Section */}
                <section id="features" className="py-32 bg-slate-950 relative">
                    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                        <div className="text-center mb-20">
                            <motion.span
                                initial={{ opacity: 0 }}
                                whileInView={{ opacity: 1 }}
                                className="text-orange-500 font-bold tracking-widest uppercase text-sm"
                            >
                                Powerful Core
                            </motion.span>
                            <h2 className="text-4xl lg:text-6xl font-black mt-4 mb-6 tracking-tight">Everything you need to scale</h2>
                            <p className="text-slate-400 max-w-2xl mx-auto text-lg">
                                Built by restaurateurs for restaurateurs. We handled the tech so you can handle the taste.
                            </p>
                        </div>

                        <motion.div
                            variants={containerVariants}
                            initial="hidden"
                            whileInView="visible"
                            viewport={{ once: true, margin: "-100px" }}
                            className="grid grid-cols-1 md:grid-cols-3 gap-8"
                        >
                            {features.map((feature, idx) => (
                                <motion.div
                                    key={idx}
                                    variants={itemVariants}
                                    whileHover={{ y: -8, transition: { duration: 0.3 } }}
                                    className="p-10 rounded-[2rem] bg-slate-900/40 border border-white/5 hover:border-orange-500/30 transition-all group backdrop-blur-sm relative overflow-hidden"
                                >
                                    <div className="absolute inset-0 bg-gradient-to-br from-orange-500/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity" />
                                    <div className="w-14 h-14 rounded-2xl bg-orange-600/10 border border-orange-500/20 flex items-center justify-center text-orange-500 mb-8 group-hover:bg-orange-600 group-hover:text-white transition-all shadow-lg group-hover:shadow-orange-600/40">
                                        {feature.icon}
                                    </div>
                                    <h3 className="text-2xl font-bold mb-4 text-white group-hover:text-orange-400 transition-colors">{feature.title}</h3>
                                    <p className="text-slate-400 leading-relaxed text-lg">
                                        {feature.description}
                                    </p>
                                </motion.div>
                            ))}
                        </motion.div>
                    </div>
                </section>

                {/* Pricing Section */}
                <section id="pricing" className="py-32 bg-slate-950 relative overflow-hidden">
                    <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] bg-blue-600/10 blur-[150px] rounded-full" />
                    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative">
                        <div className="text-center mb-20">
                            <h2 className="text-4xl lg:text-6xl font-black mb-6 tracking-tight">Transparent Pricing</h2>
                            <p className="text-slate-400 max-w-2xl mx-auto text-lg">
                                Choose the plan that fits your kitchen's ambition. No hidden fees.
                            </p>
                        </div>

                        <motion.div
                            variants={containerVariants}
                            initial="hidden"
                            whileInView="visible"
                            viewport={{ once: true }}
                            className="grid grid-cols-1 md:grid-cols-3 gap-8 items-stretch"
                        >
                            {pricingPlans.map((plan, idx) => (
                                <motion.div
                                    key={idx}
                                    variants={itemVariants}
                                    className={`relative p-10 rounded-[2.5rem] border ${plan.popular ? 'border-orange-500/50 bg-slate-900/60 shadow-[0_0_50px_rgba(249,115,22,0.1)]' : 'border-white/5 bg-slate-900/30'} backdrop-blur-xl flex flex-col transition-all hover:scale-[1.02]`}
                                >
                                    {plan.popular && (
                                        <div className="absolute -top-4 left-1/2 -translate-x-1/2 px-4 py-1.5 bg-orange-600 text-white text-xs font-black rounded-full uppercase tracking-widest shadow-lg">
                                            Most Popular
                                        </div>
                                    )}
                                    <h3 className="text-xl font-bold mb-2">{plan.name}</h3>
                                    <div className="flex items-baseline gap-1 mb-6">
                                        <span className="text-5xl font-black tracking-tighter">{plan.price}</span>
                                        {plan.price !== "Custom" && <span className="text-slate-400 font-medium">/mo</span>}
                                    </div>
                                    <p className="text-slate-400 mb-8 font-medium">{plan.description}</p>
                                    <div className="space-y-4 mb-10 flex-1">
                                        {plan.features.map((f, i) => (
                                            <div key={i} className="flex gap-3 text-sm font-medium items-center">
                                                <Shield className="w-4 h-4 text-orange-500" />
                                                <span className="text-slate-300">{f}</span>
                                            </div>
                                        ))}
                                    </div>
                                    <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                        <Button className={`w-full h-14 rounded-2xl font-black italic transition-all ${plan.popular ? 'bg-orange-600 hover:bg-orange-700 text-white shadow-xl shadow-orange-600/20' : 'bg-white/10 hover:bg-white/20 text-white border border-white/10'}`}>
                                            {plan.cta}
                                        </Button>
                                    </motion.div>
                                </motion.div>
                            ))}
                        </motion.div>
                    </div>
                </section>

                {/* Dynamic CTA Section */}
                <section className="py-32 px-4">
                    <div className="max-w-7xl mx-auto">
                        <motion.div
                            initial={{ opacity: 0, scale: 0.95 }}
                            whileInView={{ opacity: 1, scale: 1 }}
                            viewport={{ once: true }}
                            className="relative rounded-[3rem] overflow-hidden bg-slate-900 border border-white/5 p-12 lg:p-24 text-center group"
                        >
                            <div className="absolute inset-0 bg-gradient-to-br from-orange-600/30 via-transparent to-blue-600/20 opacity-50 group-hover:opacity-70 transition-opacity" />
                            <div className="relative z-10">
                                <h2 className="text-4xl lg:text-7xl font-black mb-8 italic tracking-tighter">Ready to fire up the kitchen?</h2>
                                <p className="text-xl lg:text-2xl text-slate-300 mb-12 max-w-2xl mx-auto italic font-medium leading-relaxed">
                                    Join the fleet of elite restaurants using RestaurantOS.
                                    Scale your operation with precision.
                                </p>
                                <Link href="/register">
                                    <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} className="inline-block">
                                        <Button size="lg" className="h-20 px-12 rounded-full bg-orange-600 hover:bg-orange-700 text-2xl italic font-black border-none group shadow-2xl shadow-orange-600/40">
                                            Join the Mission
                                            <ArrowRight className="ml-3 w-8 h-8 group-hover:translate-x-2 transition-transform" />
                                        </Button>
                                    </motion.div>
                                </Link>
                                <div className="mt-12 flex items-center justify-center gap-8 opacity-50">
                                    <div className="h-px w-12 bg-white/20" />
                                    <span className="text-sm font-bold tracking-[0.2em] uppercase">Trusted by 200+ Kitchens</span>
                                    <div className="h-px w-12 bg-white/20" />
                                </div>
                            </div>
                        </motion.div>
                    </div>
                </section>
            </main>

            <footer className="border-t border-slate-900/50 py-20 bg-slate-950">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="grid grid-cols-1 md:grid-cols-4 gap-12 mb-16">
                        <div className="col-span-1 md:col-span-2">
                            <div className="flex items-center gap-2 mb-6">
                                <ChefHat className="text-orange-500 w-8 h-8" />
                                <span className="text-2xl font-black tracking-tight italic">RestaurantOS</span>
                            </div>
                            <p className="text-slate-400 max-w-sm text-lg leading-relaxed">
                                Defining the future of restaurant management.
                                Built for high-performance culinary teams.
                            </p>
                        </div>
                        <div>
                            <h4 className="font-bold mb-6 text-white uppercase tracking-widest text-sm">Product</h4>
                            <ul className="space-y-4 text-slate-400">
                                <li><Link href="#features" className="hover:text-orange-500 transition-colors">Features</Link></li>
                                <li><Link href="#pricing" className="hover:text-orange-500 transition-colors">Pricing</Link></li>
                                <li><Link href="#" className="hover:text-orange-500 transition-colors">Beta Access</Link></li>
                            </ul>
                        </div>
                        <div>
                            <h4 className="font-bold mb-6 text-white uppercase tracking-widest text-sm">Company</h4>
                            <ul className="space-y-4 text-slate-400">
                                <li><Link href="#" className="hover:text-orange-500 transition-colors">About</Link></li>
                                <li><Link href="#" className="hover:text-orange-500 transition-colors">Privacy</Link></li>
                                <li><Link href="#" className="hover:text-orange-500 transition-colors">Terms</Link></li>
                            </ul>
                        </div>
                    </div>
                    <div className="flex flex-col md:flex-row justify-between items-center gap-8 pt-12 border-t border-white/5 text-sm text-slate-500 font-medium">
                        <span>© 2026 RestaurantOS Inc. All systems operational.</span>
                        <div className="flex gap-6">
                            <Link href="#" className="hover:text-white transition-colors underline decoration-orange-500/30 underline-offset-4">Security</Link>
                            <Link href="#" className="hover:text-white transition-colors underline decoration-orange-500/30 underline-offset-4">Uptime</Link>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    )
}
